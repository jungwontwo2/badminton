// src/api.js
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8081' // 기본 API 서버 주소
});

// 요청 인터셉터: 모든 요청에 토큰을 추가하는 로직
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('jwtToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

export default api;