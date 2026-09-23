import { vi } from 'vitest';

// A tiny fake backend for tests: routes are matched on "METHOD /path" (after the /api/v1 prefix).
// A handler returns { status, body } or a plain body (status 200). Every call is recorded in `calls`.
export function fakeServer(routes) {
  const calls = [];
  const fetchMock = vi.fn(async (url, init = {}) => {
    const method = init.method ?? 'GET';
    const path = String(url).replace(/^.*\/api\/v1/, '');
    calls.push({ method, path, headers: init.headers ?? {}, body: init.body });
    const handler = routes[`${method} ${path}`] ?? routes[`${method} ${path.split('?')[0]}`];
    if (!handler) {
      return new Response(JSON.stringify({ message: `no fake route for ${method} ${path}` }), { status: 404 });
    }
    const result = await handler({ path, init, calls });
    const { status = 200, body } = result && Object.hasOwn(result, 'status') ? result : { body: result };
    return new Response(body === undefined ? '' : JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } });
  });
  vi.stubGlobal('fetch', fetchMock);
  return { calls, fetchMock };
}
