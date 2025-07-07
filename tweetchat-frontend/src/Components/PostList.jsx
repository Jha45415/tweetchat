import React from 'react';
import { Link } from 'react-router-dom';
import LikeButton from './LikeButton';
import postService from '../Services/PostService';
import './PostList.css';


function PostList({ posts, isLoading, error, emptyMessage, onPostDeleted, loggedInUsername }) {

    const formatTimestamp = (timestamp) => {
        return new Date(timestamp).toLocaleString();
    };

    const handleDelete = async (postId) => {

        if (window.confirm("Are you sure you want to delete this post?")) {
            try {

                await postService.deletePost(postId);


                if (onPostDeleted) {
                    onPostDeleted(postId);
                }
            } catch (err) {
                console.error("Failed to delete post:", err);

                alert("Error: Could not delete post. You may not be the author.");
            }
        }
    };

    if (isLoading) return <p>Loading posts...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div className="post-list-container">
            {posts.length > 0 ? (
                posts.map((post) => (
                    <div key={post.id} className="post-card">


                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <p className="post-header">
                                <Link to={`/profile/${post.authorUsername}`}>
                                    <strong>@{post.authorUsername}</strong>
                                </Link>
                            </p>


                            {loggedInUsername === post.authorUsername && (
                                <button
                                    title="Delete Post"
                                    onClick={() => handleDelete(post.id)}
                                    title="Delete Post"
                                    style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '1.2rem', color: '#657786',padding: '0.25rem',
                                        borderRadius: '50%'  }}
                                    onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#e0245e20'}
                                    onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                                >
                                    🗑️ {/* Unicode for a trash can icon */}
                                </button>
                            )}
                        </div>

                        <p className="post-text">{post.text}</p>

                        {post.imageUrl && (
                            <img
                                src={`http://localhost:8080${post.imageUrl}`}
                                alt="Post attachment"
                                style={{ maxWidth: '100%', borderRadius: '8px', marginTop: '1rem' }}
                            />
                        )}

                        <div className="post-footer">
                            <small>{formatTimestamp(post.timestamp)}</small>
                            <div className="like-button-container">
                                <LikeButton
                                    postId={post.id}
                                    initialLikesCount={post.likesCount}
                                    initialIsLiked={post.likedByCurrentUser}
                                />
                            </div>
                        </div>
                    </div>
                ))
            ) : (
                <p>{emptyMessage}</p>
            )}
        </div>
    );
}

export default PostList;