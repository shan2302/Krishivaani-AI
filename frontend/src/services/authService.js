const API_BASE = 'http://localhost:8080/api/auth';

export async function loginUser(email, password) {
    const res = await fetch(`${API_BASE}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Login failed');
    return data; // { token, name, email, message }
}

export async function registerUser(name, email, password) {
    const res = await fetch(`${API_BASE}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password }),
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Registration failed');
    return data;
}

export function saveToken(token) {
    localStorage.setItem('kv_token', token);
}

export function getToken() {
    return localStorage.getItem('kv_token');
}

export function removeToken() {
    localStorage.removeItem('kv_token');
}

export function isLoggedIn() {
    return !!getToken();
}
