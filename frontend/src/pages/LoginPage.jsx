import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser, saveToken } from "../services/authService";
import AuthLayout from "../components/AuthLayout";

function validate(email, password) {
    const errors = {};
    if (!email.trim()) errors.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) errors.email = 'Enter a valid email address';
    if (!password) errors.password = 'Password is required';
    else if (password.length < 8) errors.password = 'Password must be at least 8 characters';
    return errors;
}

export default function LoginPage() {
    const navigate = useNavigate();
    const [form, setForm] = useState({ email: '', password: '' });
    const [errors, setErrors] = useState({});
    const [serverError, setServerError] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);

    function handleChange(e) {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
        if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setServerError('');
        const validationErrors = validate(form.email, form.password);
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }
        setLoading(true);
        try {
            const data = await loginUser(form.email, form.password);
            saveToken(data.token);
            navigate('/');
        } catch (err) {
            setServerError(err.message);
        } finally {
            setLoading(false);
        }
    }
    return (
        <AuthLayout title="Welcome Back" subtitle="Sign in to your account">
            <form onSubmit={handleSubmit} noValidate>
                {serverError && (
                    <div className="alert alert-danger py-2 small" role="alert">{serverError}</div>
                )}
                <div className="mb-3">
                    <label htmlFor="login-email" className="form-label fw-medium">Email Address</label>
                    <input id="login-email" type="email" name="email"
                        className={`form-control ${errors.email ? 'is-invalid' : form.email ? 'is-valid' : ''}`}
                        placeholder="you@example.com" value={form.email}
                        onChange={handleChange} autoComplete="email"
                    />
                    {errors.email && <div className="invalid-feedback">{errors.email}</div>}

                </div>
                <div className="mb-4">
                    <div className="d-flex justify-content-between align-items-center">
                        <label htmlFor="login-password" className="form-label fw-medium mb-0">Password</label>
                        <a href="#" className="small text-decoration-none" style={{ color: '#2d6a4f' }}>
                            Forgot password?
                        </a>
                    </div>
                    <div className="input-group mt-1">
                        <input
                            id="login-password" name="password"
                            type={showPassword ? 'text' : 'password'}
                            className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                            placeholder="••••••••" value={form.password}
                            onChange={handleChange} autoComplete="current-password"
                        />
                        <button type="button" className="btn btn-outline-secondary"
                            onClick={() => setShowPassword(v => !v)}
                            aria-label={showPassword ? 'Hide password' : 'Show password'}>
                            {showPassword ? '🙈' : '👁️'}
                        </button>
                        {errors.password && <div className="invalid-feedback">{errors.password}</div>}
                    </div>
                </div>
                <button type="submit" className="btn w-100 fw-semibold" disabled={loading}
                    style={{ background: 'linear-gradient(135deg, #1a472a, #52b788)', color: '#fff', borderRadius: '8px', padding: '10px' }}
                >
                    {loading ?
                        <><span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Signing in.....
                        </> : 'Sign In'
                    }
                </button>
                <p className="text-center text-muted mt-3 mb-0 small">
                    Don't have an account?{' '}
                    <Link to="/register" className="fw-semibold text-decoration-none"
                        style={{ color: "#2d6a4f" }}>Create Account</Link>
                </p>
            </form>
        </AuthLayout>
    )

}