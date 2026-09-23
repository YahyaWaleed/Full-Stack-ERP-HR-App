// the only place that touches the JWT in localStorage -- everything else goes through AuthContext (components)
// or these functions (apiClient, which isn't a component and can't use hooks)
const TOKEN_KEY = 'token';

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

// reads the payload out of a JWT without needing any extra library
export function decodeToken(token) {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
  } catch {
    return null;
  }
}

// a token counts as valid only if it decodes and its exp (seconds since epoch) is still in the future
export function isTokenValid(token) {
  const decoded = token && decodeToken(token);
  return Boolean(decoded && decoded.exp && decoded.exp * 1000 > Date.now());
}
