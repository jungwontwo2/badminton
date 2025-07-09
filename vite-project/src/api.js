import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8081'
});

api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

api.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;

        // ⭐ [수정] 이제 서버가 401 에러를 보내주므로, 401을 감지하도록 수정합니다.
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshToken = localStorage.getItem('refreshToken');
                const res = await axios.post('http://localhost:8081/auth/refresh', { refreshToken });

                const newAccessToken = res.data.accessToken;
                localStorage.setItem('accessToken', newAccessToken);

                // 새로운 토큰으로 원래 요청의 헤더를 교체
                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;

                // 원래 실패했던 요청을 새로운 토큰으로 재시도
                return api(originalRequest);

            } catch (refreshError) {
                console.error("세션이 만료되었습니다. 다시 로그인해주세요.", refreshError);
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                window.location.href = '/';
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export default api;
