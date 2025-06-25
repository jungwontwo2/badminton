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

                    // ⭐ [수정] : 응답 데이터에서 토큰과 사용자 정보를 분리
                    const { token, user } = response.data;

                    // ⭐ [수정] : 받은 토큰을 localStorage에 저장
                    localStorage.setItem('jwtToken', token);

                    onLoginSuccess(user); // App.jsx의 user 상태는 사용자 정보로 업데이트
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