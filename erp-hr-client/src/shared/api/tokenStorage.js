// The access token lives only in memory (not localStorage), so an XSS bug can't lift a long-lived
// credential from storage. The session survives reloads through the HttpOnly refresh-token cookie,
// which JavaScript can't read at all (review 9.5).
let accessToken = null;

export function getToken() {
  return accessToken;
}

export function setToken(token) {
  accessToken = token;
}

export function clearToken() {
  accessToken = null;
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
