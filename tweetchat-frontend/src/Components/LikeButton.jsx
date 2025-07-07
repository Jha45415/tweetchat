
import React, { useState } from 'react';
import postService from '../Services/PostService';

function LikeButton({ postId, initialLikesCount, initialIsLiked }) {
    const [likesCount, setLikesCount] = useState(initialLikesCount);
    const [isLiked, setIsLiked] = useState(initialIsLiked);
    const [isLoading, setIsLoading] = useState(false);

    const handleLike = async () => {
        setIsLoading(true);
        try {
            await postService.likePost(postId);
            setIsLiked(true);
            setLikesCount(likesCount + 1);
        } catch (error) {
            console.error("Failed to like post:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleUnlike = async () => {
        setIsLoading(true);
        try {
            await postService.unlikePost(postId);
            setIsLiked(false);
            setLikesCount(likesCount - 1);
        } catch (error) {
            console.error("Failed to unlike post:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const onButtonClick = () => {
        if (isLiked) {
            handleUnlike();
        } else {
            handleLike();
        }
    };

    return (
        <button onClick={onButtonClick} disabled={isLoading} style={{ color: isLiked ? 'red' : 'black' }}>
            ♥ {likesCount}
        </button>
    );
}

export default LikeButton;