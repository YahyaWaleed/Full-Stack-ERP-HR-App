import { Navigate } from 'react-router-dom';


// function to protect routes that require authentication. If the user is not authenticated (i.e. no token in localStorage), they will be redirected to the login page.
function ProtectedRoute({ children }) { // children is the component that is being protected, e.g. <Dashboard /> or <Employees />
  const token = localStorage.getItem('token'); // get the jwt from localStorage

  if (!token) {
    return <Navigate to="/login" replace />; // navigate is to redirect the user to the login page if they are not authenticated. The replace prop is used to replace the current entry in the history stack instead of adding a new one, so that the user cannot go back to the protected route after logging out.
  }

  return children; // if the user is authenticated, render the protected component (children)
}

export default ProtectedRoute;