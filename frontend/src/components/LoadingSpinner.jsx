import './LoadingSpinner.css';

function LoadingSpinner() {
  return (
    <div className="kv-loading" role="status" aria-live="polite">
      <span className="kv-spinner" aria-hidden="true"></span>
      <span>KrishiVaani is thinking...</span>
    </div>
  );
}

export default LoadingSpinner;