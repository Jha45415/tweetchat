

import React, { useState } from 'react';
import { Routes, Route } from 'react-router-dom';


import Navbar from './Components/Navbar';
import ProtectedRoute from './Components/ProtectedRoute';


import HomePage from './Pages/HomePage';
import LoginPage from './Pages/LoginPage';
import RegisterPage from './Pages/RegisterPage';
import SearchPage from './Pages/SearchPage';
import UserProfilePage from './Pages/UserProfilePage';
import FollowingPage from './Pages/FollowingPage';
import FollowersPage from './Pages/FollowerPage';


import './App.css';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('authToken'));

    return (
        <div>
            <Navbar isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />
            <main className="container" style={{ paddingTop: '2rem' }}>
                <Routes>

                    <Route
                        path="/login"
                        element={<LoginPage setIsLoggedIn={setIsLoggedIn} />}
                    />
                    <Route path="/register" element={<RegisterPage />} />


                    <Route element={<ProtectedRoute />}>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/search" element={<SearchPage />} />

                        <Route path="/profile/:username" element={<UserProfilePage />} />
                        <Route path="/profile/:username/following" element={<FollowingPage />} />
                        <Route path="/profile/:username/followers" element={<FollowersPage />} />
                    </Route>


                    <Route path="*" element={<h2>404 - Not Found</h2>} />
                </Routes>
            </main>


            <footer style={{
                backgroundColor: '#343a40',
                color: 'white',
                padding: '1rem',
                textAlign: 'center',
                marginTop: 'auto'
            }}>
                <p>© {new Date().getFullYear()} Aashish Jha - OTH Regensburg (ICS) Web Technology Project</p>
            </footer>
        </div>
    );
}

export default App;