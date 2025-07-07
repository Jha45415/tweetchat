

import axios from 'axios';


const getBaseURL = () => {

    if (import.meta.env.DEV) {

        return import.meta.env.VITE_API_URL;
    }


    return '__VITE_API_URL__';
};


const apiClient = axios.create({
    baseURL: getBaseURL(),
    headers: {
        'Content-Type': 'application/json',
    },
});


apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('authToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default apiClient;