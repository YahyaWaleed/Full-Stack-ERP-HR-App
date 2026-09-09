
const BASE_URL = 'http://localhost:8080/api';

// this function will allow me to write request('/employees', { method: 'GET' }) instead of fetch('http://localhost:8080/api/employees', { method: 'GET' })
// path = the endpoint, e.g. '/employees'  
// options = the fetch options, e.g. { method: 'GET' }
async function request(path, options = {}) {
  const token = localStorage.getItem('token');  // get the jwt from localStorage

  // make the fetch request with the base URL and the path, and add the Authorization header if the token exists
  const response = await fetch(`${BASE_URL}${path}`, {
    ...options, // takes all options passed in and spreads them in the fetch request
    headers: {
      'Content-Type': 'application/json', // tells the backend that we are sending JSON data

      // if the token exists, add the Authorization header with the Bearer token; else, don't add the Authorization header
      ...(token ? { Authorization: `Bearer ${token}` } : {}),

      ...options.headers, // allows the user to override the default headers if they want to, e.g. if they want to send a different Content-Type or add additional headers like 'name': 'value'
    },
  });

  // if the response is 401 (Unauthorized) or 403 (Forbidden), remove the token from localStorage and redirect to the login page
  if (response.status === 401 || response.status === 403) {
    localStorage.removeItem('token'); // remove token from localStorage
    window.location.href = '/login'; // redirect to the login page
    return; // stop the function from continuing to execute (after redirect, the user will be on the login page and the rest of the code won't run)
  }

  // if server returns an error, throw an error with the message from the backend (if it exists) or a generic message
  if (!response.ok) {
      const text = await response.text();
      let message = `Request failed (${response.status})`;
      try {
        const parsed = JSON.parse(text);
        message = parsed.message || message;
      } catch {
        // response wasn't JSON — keep the default message
      }
      throw new Error(message);
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
  }


// this object will be used to make API calls in the frontend, e.g. apiClient.get('/employees') or apiClient.post('/employees', { name: 'John Doe' })
export const apiClient = {
  get: (path) => request(path, { method: 'GET' }),
  post: (path, body) => request(path, { method: 'POST', body: JSON.stringify(body) }),
  put: (path, body) => request(path, { method: 'PUT', body: JSON.stringify(body) }),
  patch: (path, body) => request(path, { method: 'PATCH', body: JSON.stringify(body) }),
  delete: (path) => request(path, { method: 'DELETE' }),
};


