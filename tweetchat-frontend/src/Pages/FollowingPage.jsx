import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import userService from '../Services/UserService';
import UserList from '../Components/UserList';

function FollowingPage() {
    const [users, setUsers] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const { username } = useParams();

    useEffect(() => {
        const fetchFollowing = async () => {
            try {
                const response = await userService.getFollowing(username);
                setUsers(response.data);
            } catch (err) {
                console.error("Error fetching data:", err); // Log the actual error
                setError('Failed to load following list.');
            }
             finally {
                setIsLoading(false);
            }
        };
        fetchFollowing();
    }, [username]);

    return (
        <div>
            <h2>Following @{username}</h2>
            <UserList users={users} isLoading={isLoading} error={error} />
        </div>
    );
}
export default FollowingPage;