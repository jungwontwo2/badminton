// src/AuthRedirectHandler.jsx
import React, { useEffect, useRef } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

function AuthRedirectHandler({ onLoginSuccess }) {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const effectRan = useRef(false);

    useEffect(() => {
        if (effectRan.current === true) return;

        const code = searchParams.get('code');
        if (code) {
            axios.get(`http://localhost:8081/auth/kakao/callback?code=${code}`)
                .then(response => {
                    console.log('로그인 성공:', response.data);

                    const { accessToken, refreshToken, user } = response.data;

                    // ⭐ [수정] 두 종류의 토큰을 각각 다른 이름으로 저장합니다.
                    localStorage.setItem('accessToken', accessToken);
                    localStorage.setItem('refreshToken', refreshToken);

                    onLoginSuccess(user);
                    navigate('/');
                })
                .catch(error => {
                    console.error('로그인 처리 실패:', error);
                    navigate('/');
                });
        }

        return () => { effectRan.current = true; };
    }, [searchParams, navigate, onLoginSuccess]);

    return <div>로그인 처리 중...</div>;
}

export default AuthRedirectHandler;