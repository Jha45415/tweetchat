import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import userService from '../Services/UserService';
import UserList from '../Components/UserList';

function FollowersPage() {
    const [users, setUsers] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const { username } = useParams();

    useEffect(() => {
        const fetchFollowers = async () => {
            try {
                const response = await userService.getFollowers(username);
                setUsers(response.data);
            }catch (err) {
                console.error("Error fetching data:", err);
                setError('Failed to load follower list.');
            }
            finally {
                setIsLoading(false);
            }
        };
        fetchFollowers();
    }, [username]);

    return (
        <div>
            <h2>Followers of @{username}</h2>
            <UserList users={users} isLoading={isLoading} error={error} />
        </div>
    );
}
export default FollowersPage;