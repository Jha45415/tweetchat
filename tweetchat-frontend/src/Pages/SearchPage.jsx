

import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import userService from '../Services/UserService';
import FollowButton from '../Components/FollowButton';

function SearchPage() {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);
    const [error, setError] = useState('');

    const handleSearch = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const response = await userService.search(query);
            setResults(response.data);
        } catch (err) {
            console.error("Search failed:", err);
            setError("Failed to perform search.");
        }
    };

    return (
        <div>
            <h2>Search for Users</h2>
            <form onSubmit={handleSearch}>
                <input
                    type="text"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="Enter username..."
                    style={{ marginRight: '1rem' }}
                />
                <button type="submit">Search</button>
            </form>

            {error && <p style={{color: 'red'}}>{error}</p>}

            <div style={{marginTop: '2rem'}}>
                <h3>Results</h3>
                {results.length > 0 ? (
                    results.map(user => (
                        <div key={user.id} style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.5rem', borderBottom: '1px solid #eee'}}>


                            <span>
                <Link to={`/profile/${user.username}`}>
                  @{user.username}
                </Link>
              </span>

                            <FollowButton username={user.username} initialIsFollowing={user.followedByCurrentUser} />
                        </div>
                    ))
                ) : (
                    <p>No users found for this query.</p>
                )}
            </div>
        </div>
    );
}

export default SearchPage;