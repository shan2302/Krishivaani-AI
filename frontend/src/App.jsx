import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import { isLoggedIn } from './services/authService';

function Dashboard() {
  return (
    <div className="d-flex align-items-center justify-content-center min-vh-100">
      <div className="text-center">
        <h1 className="text-warning">🌾 KrishiVaani AI</h1>
        <p className="text-muted">Dashboard coming soon...</p>
        <button
          className="btn btn-outline-danger btn-sm"
          onClick={() => { localStorage.removeItem('kv_token'); window.location.href = '/login'; }}
        >
          Logout
        </button>
      </div>
    </div>
  );
}

function ProtectedRoute({ children }) {
  return isLoggedIn() ? children : <Navigate to="/login" replace />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
