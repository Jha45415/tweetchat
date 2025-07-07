import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../Services/authService';

function LoginPage({ setIsLoggedIn }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            const response = await authService.login({ username, password });

            if (response.data && response.data.accessToken) {

                localStorage.setItem('authToken', response.data.accessToken);


                localStorage.setItem('username', username);


                setIsLoggedIn(true);


                navigate('/');
            }
        } catch (err) {
            console.error('Login failed:', err);
            setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '50px auto', padding: '2rem', border: '1px solid #e1e8ed', borderRadius: '8px', backgroundColor: 'white' }}>
            <h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>Login</h2>
            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: '1rem' }}>
                    <label htmlFor="username" style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 'bold' }}>Username</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        style={{ width: '100%', padding: '0.75rem', boxSizing: 'border-box', border: '1px solid #ccc', borderRadius: '4px' }}
                    />
                </div>
                <div style={{ marginBottom: '1.5rem' }}>
                    <label htmlFor="password" style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 'bold' }}>Password</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        style={{ width: '100%', padding: '0.75rem', boxSizing: 'border-box', border: '1px solid #ccc', borderRadius: '4px' }}
                    />
                </div>
                {error && <p style={{ color: '#e0245e', textAlign: 'center', marginBottom: '1rem' }}>{error}</p>}
                <button
                    type="submit"
                    disabled={isLoading}
                    style={{
                        width: '100%',
                        padding: '0.75rem',
                        backgroundColor: '#1da1f2',
                        color: 'white',
                        border: 'none',
                        borderRadius: '9999px',
                        fontWeight: 'bold',
                        fontSize: '1rem',
                        cursor: 'pointer'
                    }}
                >
                    {isLoading ? 'Logging in...' : 'Login'}
                </button>
            </form>
            <p style={{ marginTop: '1.5rem', textAlign: 'center', color: '#657786' }}>
                Don't have an account? <Link to="/register" style={{ color: '#1da1f2', textDecoration: 'none' }}>Register here</Link>
            </p>
        </div>
    );
}

export default LoginPage;