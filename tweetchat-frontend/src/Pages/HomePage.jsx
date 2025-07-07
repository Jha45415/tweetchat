import React, { useState, useEffect } from 'react';
import CreatePostForm from '../Components/CreatePostForm';
import PostList from '../Components/PostList';
import postService from '../Services/PostService';

function HomePage() {
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const loggedInUsername = localStorage.getItem('username');

    const fetchPosts = async (pageNum) => {
        if (pageNum === 0) setIsLoading(true);
        try {
            const response = await postService.getFeed(pageNum);
            setPosts(prevPosts => pageNum === 0 ? response.data : [...prevPosts, ...response.data]);
            setHasMore(response.data.length === 20);
        } catch (err) {
            console.error("Failed to fetch feed:", err);
            setError("Could not load your feed. Please try again later.");
        } finally {
            if (pageNum === 0) setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchPosts(0);
    }, []);

    const loadMorePosts = () => {
        const nextPage = page + 1;
        setPage(nextPage);
        fetchPosts(nextPage);
    };

    const handlePostCreated = (newPost) => {
        setPosts(currentPosts => [newPost, ...currentPosts]);
    };

    const handlePostDeleted = (deletedPostId) => {
        setPosts(currentPosts => currentPosts.filter(post => post.id !== deletedPostId));
    };

    return (
        <div>
            <h2>Home</h2>
            <CreatePostForm onPostCreated={handlePostCreated} />
            <hr style={{ margin: '2rem 0' }} />
            <h3>Your Timeline</h3>
            <PostList
                posts={posts}
                isLoading={isLoading}
                error={error}
                emptyMessage="Your feed is empty. Follow some users to see their posts here!"
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

export default HomePage;