import apiClient from './apiClient';
const login = async (credentials) => {
    return apiClient.post('/auth/login', credentials);
};
const register = async (userData) => {
    return apiClient.post('/auth/register', userData);
};
export default {
    login,
    register,
};