import React from 'react';
import { Link } from 'react-router-dom';
import FollowButton from './FollowButton';

function UserList({ users, isLoading, error }) {
    if (isLoading) return <p>Loading users...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div>
            {users.length > 0 ? (
                users.map(user => (
                    <div key={user.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.75rem', borderBottom: '1px solid #eee' }}>
                        <Link to={`/profile/${user.username}`} style={{ fontWeight: 'bold', textDecoration: 'none', color: 'inherit' }}>
                            @{user.username}
                        </Link>
                        <FollowButton username={user.username} initialIsFollowing={user.followedByCurrentUser} />
                    </div>
                ))
            ) : (
                <p>No users to display.</p>
            )}
        </div>
    );
}

export default UserList;