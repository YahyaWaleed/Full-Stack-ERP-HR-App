// shows an API error: the message plus any field-level validation errors from the backend
function ErrorMessage({ error }) {
  if (!error) return null;
  const message = typeof error === 'string' ? error : error.message;
  const fieldErrors = typeof error === 'string' ? [] : error.fieldErrors || [];
  return (
    <div className="error-message" role="alert">
      <p>{message}</p>
      {fieldErrors.length > 0 && (
        <ul>
          {fieldErrors.map((e) => <li key={e}>{e}</li>)}
        </ul>
      )}
    </div>
  );
}

export default ErrorMessage;
