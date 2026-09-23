import { useCallback, useEffect, useState, useSyncExternalStore } from 'react';
import { request } from './client';

// A small shared data layer (review 6.4 / 7.12):
//  - responses are cached per path for `ttl` ms, so /payroll-periods is fetched once, not by six pages;
//  - concurrent requests for the same path share one fetch (deduplication);
//  - that fetch is aborted when nobody is waiting for it any more (fast navigation);
//  - invalidate('/employees') after a write marks matching entries stale and refetches mounted views.

const DEFAULT_TTL = 30_000;
const cache = new Map();        // path -> { data, fetchedAt }
const inFlight = new Map();     // path -> { promise, controller, waiting }
let generation = 0;             // bumps on every invalidate so mounted hooks refetch
const listeners = new Set();

function subscribe(listener) {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

function getGeneration() {
  return generation;
}

// drops cached entries whose path starts with any of the prefixes and tells mounted hooks to refetch
export function invalidate(...prefixes) {
  for (const key of [...cache.keys()]) {
    if (prefixes.length === 0 || prefixes.some((p) => key.startsWith(p))) {
      cache.delete(key);
    }
  }
  generation += 1;
  listeners.forEach((l) => l());
}

// for tests and logout
export function clearApiCache() {
  cache.clear();
  inFlight.forEach((entry) => entry.controller.abort());
  inFlight.clear();
}

function fresh(path, ttl) {
  const entry = cache.get(path);
  return entry && Date.now() - entry.fetchedAt < ttl ? entry : null;
}

// shared, reference-counted fetch: the network call is only aborted when every waiter has gone
function fetchShared(path) {
  let entry = inFlight.get(path);
  if (!entry) {
    const controller = new AbortController();
    entry = { controller, waiting: 0 };
    entry.promise = request(path, { signal: controller.signal })
      .then((data) => {
        cache.set(path, { data, fetchedAt: Date.now() });
        return data;
      })
      .finally(() => {
        if (inFlight.get(path) === entry) inFlight.delete(path);
      });
    entry.promise.catch(() => {}); // an aborted fetch nobody waits for anymore is not an error
    inFlight.set(path, entry);
  }
  entry.waiting += 1;
  let released = false;
  const release = () => {
    if (released) return;
    released = true;
    entry.waiting -= 1;
    if (entry.waiting === 0 && inFlight.get(path) === entry) {
      entry.controller.abort();
      inFlight.delete(path);
    }
  };
  return { promise: entry.promise, release };
}

// const { data, error, loading, reload } = useApi('/employees?page=0') -- pass null to skip the call.
// A cache hit is read straight from the cache during render; only network results go through state.
export function useApi(path, { ttl = DEFAULT_TTL } = {}) {
  const gen = useSyncExternalStore(subscribe, getGeneration);
  const [reloads, setReloads] = useState(0);
  const [result, setResult] = useState({ key: null, path: null, data: undefined, error: null });

  const key = path ? `${path}#${gen}#${reloads}` : null; // a new key = fetch again
  const hit = path ? fresh(path, ttl) : null;
  const needsFetch = Boolean(path) && !hit; // also true once a cached entry this view showed has expired

  useEffect(() => {
    if (!needsFetch) return undefined;
    let active = true; // a late response for a path we've already left is ignored
    const { promise, release } = fetchShared(path);
    promise
      .then((data) => active && setResult({ key, path, data, error: null }))
      .catch((error) => {
        if (active && error.name !== 'AbortError') setResult({ key, path, data: undefined, error });
      });
    return () => {
      active = false;
      release();
    };
  }, [key, path, needsFetch]);

  const own = result.key === key ? result : null;
  // while refetching the same path (after invalidate/reload) keep showing the previous rows instead of flashing empty
  const previous = result.path === path ? result.data : undefined;

  const reload = useCallback(() => {
    if (path) cache.delete(path);
    setReloads((n) => n + 1);
  }, [path]);

  return {
    data: hit ? hit.data : own ? own.data : previous,
    error: own?.error ?? null,
    loading: Boolean(path) && !hit && !own,
    reload,
  };
}
