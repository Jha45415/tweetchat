import apiClient from './apiClient';
const follow = (username) => {
    return apiClient.post(`/users/${username}/follow`);
};
const unfollow = (username) => {
    return apiClient.post(`/users/${username}/unfollow`);
};
const search = (query) => {
    return apiClient.get(`/users/search?q=${query}`);
};
const getProfile = (username) => {
    return apiClient.get(`/users/${username}`);
};
const getFollowing = (username) => {
    return apiClient.get(`/users/${username}/following`);
};

const getFollowers = (username) => {
    return apiClient.get(`/users/${username}/followers`);
};
export default {
    follow,
    unfollow,
    search,
    getProfile,
    getFollowing,
    getFollowers,
};