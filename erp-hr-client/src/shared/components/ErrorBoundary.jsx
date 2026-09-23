import { Component } from 'react';

// Catches rendering errors so one broken page shows a message instead of a blank screen (review 6.6).
// Give it a key that changes on navigation (the path) so moving to another page clears the error.
class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { error: null };
  }

  static getDerivedStateFromError(error) {
    return { error };
  }

  componentDidCatch(error, info) {
    console.error('Page crashed:', error, info.componentStack);
  }

  render() {
    if (this.state.error) {
      return (
        <div className="error-page" role="alert">
          <h1>Something went wrong on this page</h1>
          <p className="muted">The rest of the app still works. Try reloading, or go back to the dashboard.</p>
          <div className="page-actions">
            <button type="button" onClick={() => window.location.reload()}>Reload</button>
            <a className="button-link" href="/dashboard">Dashboard</a>
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}

export default ErrorBoundary;
