// src/App.jsx
import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route,Link } from 'react-router-dom';
import KakaoLogin from './KakaoLogin';
import AuthRedirectHandler from './AuthRedirectHandler';
import ProfileForm from './ProfileForm';
import AdminPage from "./AdminPage.jsx";
import api from "./api.js";

function App() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkLoginStatus = async () => {
            const token = localStorage.getItem('jwtToken');
            if (token) {
                console.log("토큰 발견! 로그인 상태 복원 시도");
                try {
                    // ⭐ [수정] : 토큰으로 내 정보를 가져오는 API 호출
                    const response = await api.get('/api/users/me');
                    setUser(response.data); // 성공 시 사용자 정보로 상태 업데이트
                } catch (error) {
                    console.error("로그인 상태 복원 실패:", error);
                    localStorage.removeItem('jwtToken'); // 유효하지 않은 토큰은 삭제
                }
            }
            setLoading(false);
        };
        checkLoginStatus();
    }, []);

    const handleLoginSuccess = (userData) => {
        setUser(userData);
    };

    const handleLogout = () => {
        localStorage.removeItem('jwtToken')
        setUser(null);
    };

    // 프로필 정보가 업데이트되면 user 상태를 갱신하는 함수
    const handleProfileUpdate = (updatedUser) => {
        setUser(updatedUser);
    };

    const getRoleKorean = (role) => {
        switch (role) {
            case 'USER': return '사용자';
            case 'ADMIN': return '관리자';
            default: return role;
        }
    };

    // 로그인 여부 및 추가 정보 입력 여부에 따라 다른 내용을 보여줄 컴포넌트
    const MainContent = () => {
        if (!user) {
            // 1. 로그인 안 한 경우
            return (
                <div>
                    <p>카카오로 로그인하여 시작하세요.</p>
                    <KakaoLogin />
                </div>
            );
        }

        // 2. 로그인은 했지만 추가 정보(club)가 없는 경우
        if (user && !user.club) {
            return <ProfileForm user={user} onProfileUpdate={handleProfileUpdate} />;
        }

        // 3. 로그인도 했고 추가 정보도 있는 경우
        return (
            <div>
                <h2>프로필 정보</h2>
                <img src={user.profileImageUrl} alt="프로필" width="100" style={{ borderRadius: '50%' }}/>
                <p><strong>닉네임:</strong> {user.nickname}</p>
                <p><strong>클럽:</strong> {user.club}</p>
                <p><strong>연령대:</strong> {user.ageGroup}</p>
                <p><strong>구 급수:</strong> {user.gradeGu}</p>
                <p><strong>시 급수:</strong> {user.gradeSi}</p>
                <p><strong>전국 급수:</strong> {user.gradeNational || '미입력'}</p>
                <p><strong>역할:</strong> {getRoleKorean(user.role)}</p>
                <p><strong>인증 상태:</strong> {user.status === 'VERIFIED' ? '✅ 인증됨' : '❌ 미인증'}</p>
                <button onClick={handleLogout}>로그아웃</button>
            </div>
        );
    };

    return (
        <BrowserRouter>
            <div style={{ padding: '20px', textAlign: 'center' }}>
                <header style={{ marginBottom: '20px', paddingBottom: '10px', borderBottom: '1px solid #eee' }}>
                    <h1 style={{ margin: 0 }}>배드민턴 MMR 시스템</h1>
                    <nav>
                        {/* ⭐ [추가] : 로그인한 사용자가 ADMIN일 경우에만 관리자 페이지 링크를 보여줌 */}
                        {user && user.role === 'ADMIN' && (
                            <Link to="/admin" style={{ marginLeft: '20px' }}>관리자 페이지</Link>
                        )}
                    </nav>
                </header>

                <Routes>
                    <Route path="/" element={<MainContent />} />
                    <Route path="/auth/kakao/callback" element={<AuthRedirectHandler onLoginSuccess={handleLoginSuccess} />} />
                    {/* ⭐ [추가] : /admin 경로에 AdminPage 컴포넌트를 연결 */}
                    <Route path="/admin" element={<AdminPage />} />
                </Routes>
            </div>
        </BrowserRouter>
    );
}

export default App;