import { useEffect, useState } from 'react';

// value, but only after it has stopped changing for `delay` ms -- keeps search boxes from firing a request per keystroke
export function useDebounced(value, delay = 300) {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const timer = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(timer);
  }, [value, delay]);
  return debounced;
}
