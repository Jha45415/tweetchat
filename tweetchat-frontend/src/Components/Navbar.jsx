

import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

function Navbar({ isLoggedIn, setIsLoggedIn }) {
    const navigate = useNavigate();
    const username = localStorage.getItem('username');

    const handleLogout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('username');
        setIsLoggedIn(false);
        navigate('/login');
    };

    return (
        <nav style={{
            padding: '0.75rem 2rem',
            background: '#ffffff',
            color: '#14171a',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            borderBottom: '1px solid #e1e8ed'
        }}>
            <div style={{ display: 'flex', alignItems: 'center' }}>

                <Link to="/" style={{
                    color: '#1da1f2',
                    marginRight: '1.5rem',
                    textDecoration: 'none',
                    fontSize: '1.5rem',
                    fontWeight: 'bold'
                }}>
                    Tweetchat
                </Link>



                {isLoggedIn && (
                    <Link to="/search" style={{
                        color: '#14171a',
                        textDecoration: 'none',
                        fontWeight: 'bold'
                    }}>
                        Search
                    </Link>
                )}
            </div>
            <div>
                {isLoggedIn ? (
                    <>
                        <Link to={`/profile/${username}`} style={{
                            color: '#14171a',
                            marginRight: '1.5rem',
                            textDecoration: 'none',
                            fontWeight: 'bold'
                        }}>
                            My Profile
                        </Link>
                        <button onClick={handleLogout} style={{
                            background: '#1da1f2',
                            border: 'none',
                            color: 'white',
                            padding: '0.5rem 1rem',
                            borderRadius: '9999px',
                            fontWeight: 'bold',
                            cursor: 'pointer'
                        }}>
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/login" style={{
                            color: '#14171a',
                            marginRight: '1rem',
                            textDecoration: 'none',
                            fontWeight: 'bold'
                        }}>
                            Login
                        </Link>
                        <Link to="/register" style={{
                            background: '#1da1f2',
                            border: '1px solid #1da1f2',
                            color: 'white',
                            padding: '0.5rem 1rem',
                            borderRadius: '9999px',
                            textDecoration: 'none',
                            fontWeight: 'bold'
                        }}>
                            Register
                        </Link>
                    </>
                )}
            </div>
        </nav>
    );
}

export default Navbar;