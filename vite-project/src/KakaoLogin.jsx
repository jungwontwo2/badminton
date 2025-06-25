// src/KakaoLogin.jsx
import React from 'react';
import kakaoLoginImage from './kakao_login_medium_narrow.png';

function KakaoLogin() {
    // .env.local 파일 등에서 관리하는 것이 좋습니다.
    const REST_API_KEY = '7acebd538d1cbe5db39aa8fc44879138'; // 본인의 REST API 키로 교체
    const REDIRECT_URI = 'http://localhost:5173/auth/kakao/callback';

    const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}&response_type=code`;

    return (
        <a href={KAKAO_AUTH_URL} style={{ border: 'none', background: 'none' }}>
            <img src={kakaoLoginImage} alt="카카오 로그인" />
        </a>
    );
}

export default KakaoLogin;
