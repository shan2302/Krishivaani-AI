import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthLayout from '../components/AuthLayout';
import { registerUser, saveToken } from '../services/authService';

function validate(name, email, password, confirmPassword) {
  const errors = {};
  if (!name.trim()) 
    errors.name = 'Full name is required';
  else if (name.trim().length < 2) 
    errors.name = 'Name must be at least 2 characters';
  if (!email.trim()) 
    errors.email = 'Email is required';
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) 
    errors.email = 'Enter a valid email address';
  if (!password) 
    errors.password = 'Password is required';
  else if (password.length < 8) 
    errors.password = 'Password must be at least 8 characters';
  else if (!/[A-Z]/.test(password)) 
    errors.password = 'Include at least one uppercase letter';
  else if (!/\d/.test(password)) 
    errors.password = 'Include at least one number';
  if (!confirmPassword) 
    errors.confirmPassword = 'Please confirm your password';
  else if (password !== confirmPassword) 
    errors.confirmPassword = 'Passwords do not match';
  return errors;
}

// test() -> true if the pattern is found in the string 
// false if the pattern is not found

function getPasswordStrength(password) {
  if (!password) 
    return { label: '', color: '', width: 0 };
  let score = 0;
  if (password.length >= 8) 
    score++;
  if (/[A-Z]/.test(password)) 
    score++;
  if (/\d/.test(password)) 
    score++;
  if (/[^A-Za-z0-9]/.test(password)) 
    score++;
  const levels = [
    { label: 'Too weak', color: '#dc3545', width: 25 },
    { label: 'Weak', color: '#fd7e14', width: 50 },
    { label: 'Good', color: '#ffc107', width: 75 },
    { label: 'Strong', color: '#198754', width: 100 },
  ];
  return levels[score - 1] || levels[0];
}

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', password: '', confirmPassword: '' });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const strength = getPasswordStrength(form.password);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setServerError('');
    const validationErrors = validate(form.name, form.email, form.password, form.confirmPassword);
    if (Object.keys(validationErrors).length > 0) { setErrors(validationErrors); return; }
    setLoading(true);
    try {
      const data = await registerUser(form.name, form.email, form.password);
      saveToken(data.token);
      navigate('/');
    } catch (err) {
      setServerError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthLayout title="Create Account" subtitle="Join KrishiVaani AI today">
      <form onSubmit={handleSubmit} noValidate>
        {serverError && <div className="alert alert-danger py-2 small" role="alert">{serverError}</div>}

        <div className="mb-3">
          <label htmlFor="reg-name" className="form-label fw-medium">Full Name</label>
          <input id="reg-name" type="text" name="name"
            className={`form-control ${errors.name ? 'is-invalid' : form.name ? 'is-valid' : ''}`}
            placeholder="Shantanu Singh" value={form.name}
            onChange={handleChange} autoComplete="name"
          />
          {errors.name && <div className="invalid-feedback">{errors.name}</div>}
        </div>

        <div className="mb-3">
          <label htmlFor="reg-email" className="form-label fw-medium">Email address</label>
          <input id="reg-email" type="email" name="email"
            className={`form-control ${errors.email ? 'is-invalid' : form.email ? 'is-valid' : ''}`}
            placeholder="you@example.com" value={form.email}
            onChange={handleChange} autoComplete="email"
          />
          {errors.email && <div className="invalid-feedback">{errors.email}</div>}
        </div>

        <div className="mb-3">
          <label htmlFor="reg-password" className="form-label fw-medium">Password</label>
          <div className="input-group">
            <input id="reg-password" name="password"
              type={showPassword ? 'text' : 'password'}
              className={`form-control ${errors.password ? 'is-invalid' : ''}`}
              placeholder="Min. 8 characters" value={form.password}
              onChange={handleChange} autoComplete="new-password"
            />
            <button type="button" className="btn btn-outline-secondary"
              onClick={() => setShowPassword(v => !v)}
              aria-label={showPassword ? 'Hide password' : 'Show password'}>
              {showPassword ? '🙈' : '👁️'}
            </button>
            {errors.password && <div className="invalid-feedback">{errors.password}</div>}
          </div>
          {form.password && (
            <div className="mt-2">
              <div className="progress" style={{ height: 4 }}>
                <div className="progress-bar"
                  style={{ width: `${strength.width}%`, backgroundColor: strength.color, transition: 'width 0.3s' }} />
              </div>
              <small style={{ color: strength.color }}>{strength.label}</small>
            </div>
          )}
        </div>

        <div className="mb-4">
          <label htmlFor="reg-confirm" className="form-label fw-medium">Confirm Password</label>
          <input id="reg-confirm" type="password" name="confirmPassword"
            className={`form-control ${errors.confirmPassword ? 'is-invalid' : form.confirmPassword && !errors.confirmPassword ? 'is-valid' : ''}`}
            placeholder="Re-enter your password" value={form.confirmPassword}
            onChange={handleChange} autoComplete="new-password"
          />
          {errors.confirmPassword && <div className="invalid-feedback">{errors.confirmPassword}</div>}
        </div>

        <button type="submit" className="btn w-100 fw-semibold" disabled={loading}
          style={{ background: 'linear-gradient(135deg, #1a472a, #52b788)', color: '#fff', borderRadius: '8px', padding: '10px' }}>
          {loading
            ? <><span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Creating account...</>
            : 'Create Account'}
        </button>

        <p className="text-center text-muted mt-3 mb-0 small">
          Already have an account?{' '}
          <Link to="/login" className="fw-semibold text-decoration-none" style={{ color: '#2d6a4f' }}>
            Sign in
          </Link>
        </p>
      </form>
    </AuthLayout>
  );
}
