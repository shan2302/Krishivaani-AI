import './ErrorState.css';

function ErrorState({ message, onRetry }) {
  return (
    <div className="kv-error" role="alert">
      <div className="kv-error-icon" aria-hidden="true">!</div>

      <div className="kv-error-content">
        <strong>Couldn't get a response</strong>
        <p>{message || 'Something went wrong. Please try again.'}</p>

        {onRetry && (
          <button
            type="button"
            className="kv-retry-button"
            onClick={onRetry}
          >
            Try again
          </button>
        )}
      </div>
    </div>
  );
}

export default ErrorState;