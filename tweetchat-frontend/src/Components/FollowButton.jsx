
import React, { useState } from 'react';
import userService from '../Services/UserService';


function FollowButton({ username, initialIsFollowing }) {
    const [isFollowing, setIsFollowing] = useState(initialIsFollowing);
    const [isLoading, setIsLoading] = useState(false);

    const handleFollow = async () => {
        setIsLoading(true);
        try {
            await userService.follow(username);
            setIsFollowing(true);
        } catch (error) {
            console.error("Failed to follow user:", error);

        } finally {
            setIsLoading(false);
        }
    };

    const handleUnfollow = async () => {
        setIsLoading(true);
        try {
            await userService.unfollow(username);
            setIsFollowing(false);
        } catch (error) {
            console.error("Failed to unfollow user:", error);
        } finally {
            setIsLoading(false);
        }
    };


    const currentUsername = "testuser";
    if (currentUsername === username) {
        return null;
    }

    if (isFollowing) {
        return (
            <button onClick={handleUnfollow} disabled={isLoading}>
                {isLoading ? '...' : 'Unfollow'}
            </button>
        );
    } else {
        return (
            <button onClick={handleFollow} disabled={isLoading} style={{backgroundColor: '#007bff', color: 'white'}}>
                {isLoading ? '...' : 'Follow'}
            </button>
        );
    }
}

export default FollowButton;