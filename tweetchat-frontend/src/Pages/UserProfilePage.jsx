import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import userService from '../Services/UserService';
import postService from '../Services/PostService';
import FollowButton from '../Components/FollowButton';
import PostList from '../Components/PostList';

function UserProfilePage() {
    const [profile, setProfile] = useState(null);
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const { username } = useParams();
    const loggedInUsername = localStorage.getItem('username');

    useEffect(() => {
        const fetchProfileData = async () => {
            setIsLoading(true);
            setError('');
            setPosts([]);
            setPage(0);
            setHasMore(true);
            try {
                const profilePromise = userService.getProfile(username);
                const postsPromise = postService.getPostsByUsername(username, 0);
                const [profileResponse, postsResponse] = await Promise.all([profilePromise, postsPromise]);

                setProfile(profileResponse.data);
                setPosts(postsResponse.data);
                setHasMore(postsResponse.data.length === 20);
            } catch (err) {
                console.error("Failed to fetch profile data:", err);
                setError("Could not load user profile. The user may not exist.");
            } finally {
                setIsLoading(false);
            }
        };
        fetchProfileData();
    }, [username]);

    const loadMorePosts = async () => {
        const nextPage = page + 1;
        try {
            const response = await postService.getPostsByUsername(username, nextPage);
            setPosts(prevPosts => [...prevPosts, ...response.data]);
            setHasMore(response.data.length === 20);
            setPage(nextPage);
        } catch (err) {
            console.error("Failed to load more posts:", err);
        }
    };

    const handlePostDeleted = (deletedPostId) => {
        setPosts(currentPosts => currentPosts.filter(post => post.id !== deletedPostId));
    };

    if (isLoading) return <p>Loading profile...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;
    if (!profile) return <p>User not found.</p>;

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem', padding: '1rem', backgroundColor: 'white', borderRadius: '8px', border: '1px solid #e1e8ed' }}>
                <div>
                    <h2 style={{ margin: 0 }}>@{profile.username}</h2>
                    <div style={{ display: 'flex', gap: '1rem', marginTop: '0.5rem', color: '#657786' }}>

                        <Link to={`/profile/${profile.username}/following`} style={{textDecoration: 'none', color: 'inherit'}}>
                            <span><strong>{profile.followingCount}</strong> Following</span>
                        </Link>

                        <Link to={`/profile/${profile.username}/followers`} style={{textDecoration: 'none', color: 'inherit'}}>
                            <span><strong>{profile.followersCount}</strong> Followers</span>
                        </Link>

                    </div>
                </div>
                {profile.username !== loggedInUsername &&
                    <FollowButton
                        username={profile.username}
                        initialIsFollowing={profile.followedByCurrentUser}
                    />
                }
            </div>
            <h3>Posts by @{profile.username}</h3>
            <PostList
                posts={posts}
                isLoading={false}
                error={null}
                emptyMessage="This user hasn't posted anything yet."
                onPostDeleted={handlePostDeleted}
                loggedInUsername={loggedInUsername}
            />
            {hasMore && !isLoading && (
                <div style={{ textAlign: 'center', margin: '2rem' }}>
                    <button onClick={loadMorePosts}>Load More</button>
                </div>
            )}
        </div>
    );
}

export default UserProfilePage;