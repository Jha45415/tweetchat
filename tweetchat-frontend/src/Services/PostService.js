import apiClient from './apiClient';
const createPost = async (formData) => {
    return apiClient.post('/posts', formData, {
        headers: {
        'Content-Type': 'multipart/form-data',
    },
});
};

const likePost = (postId) => {
    return apiClient.post(`/posts/${postId}/like`);
};
const unlikePost = (postId) => {
    return apiClient.delete(`/posts/${postId}/unlike`);
};
const deletePost = (postId) => {
    return apiClient.delete(`/posts/${postId}`);
};
const getFeed = async (page = 0) => {
    return apiClient.get(`/posts/feed?page=${page}`);
};

const getPostsByUsername = async (username, page = 0) => {
    return apiClient.get(`/posts/user/${username}?page=${page}`);
};

export default {
    createPost,
    getFeed,
    likePost,
    unlikePost,
    getPostsByUsername,
    deletePost,
};