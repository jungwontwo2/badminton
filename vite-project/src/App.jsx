import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import KakaoLogin from './KakaoLogin';
import AuthRedirectHandler from './AuthRedirectHandler';
import ProfileForm from './ProfileForm';
import AdminPage from './AdminPage';
import MatchResultForm from './MatchResultForm';
import RankingPage from './RankingPage';
import MyPage from './MyPage';
import UserProfilePage from './UserProfilePage'; // ⭐ [추가] 새로 만든 UserProfilePage 컴포넌트 import
import api from './api';

function App() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkLoginStatus = async () => {
            const token = localStorage.getItem('jwtToken');
            if (token) {
                try {
                    const response = await api.get('/api/users/me');
                    setUser(response.data);
                } catch (error) {
                    console.error("로그인 상태 복원 실패:", error);
                    localStorage.removeItem('accessToken');
                    localStorage.removeItem('refreshToken');
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
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setUser(null);
    };

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

    const MainContent = () => {
        if (!user) {
            return (
                <div>
                    <p>카카오로 로그인하여 시작하세요.</p>
                    <KakaoLogin />
                </div>
            );
        }

        if (user && !user.club) {
            return <ProfileForm user={user} onProfileUpdate={handleProfileUpdate} />;
        }

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

    if(loading) {
        return <div>앱 로딩 중...</div>;
    }

    return (
        <BrowserRouter>
            <div style={{ padding: '20px', textAlign: 'center' }}>
                <header style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    marginBottom: '20px',
                    paddingBottom: '10px',
                    borderBottom: '1px solid #eee'
                }}>
                    <h1 style={{ margin: 0 }}>
                        <Link to="/" style={{ textDecoration: 'none', color: 'inherit' }}>
                            배드민턴 MMR 시스템
                        </Link>
                    </h1>
                    <nav>
                        <Link to="/" style={{ marginRight: '20px' }}>메인</Link>
                        <Link to="/rankings" style={{ marginRight: '20px' }}>랭킹</Link>
                        {user && (
                            <Link to="/mypage" style={{ marginRight: '20px' }}>내 정보</Link>
                        )}
                        {user && user.status === 'VERIFIED' && (
                            <Link to="/record-match" style={{ marginRight: '20px' }}>경기 결과 등록</Link>
                        )}
                        {user && user.role === 'ADMIN' && (
                            <Link to="/admin">관리자 페이지</Link>
                        )}
                    </nav>
                </header>

                <Routes>
                    <Route path="/" element={<MainContent />} />
                    <Route path="/auth/kakao/callback" element={<AuthRedirectHandler onLoginSuccess={handleLoginSuccess} />} />
                    <Route path="/admin" element={<AdminPage />} />
                    <Route path="/record-match" element={<MatchResultForm />} />
                    <Route path="/rankings" element={<RankingPage />} />
                    <Route path="/mypage" element={<MyPage />} />
                    {/* ⭐ [추가] 다른 사용자의 프로필을 보여줄 동적 경로를 추가합니다. */}
                    <Route path="/profiles/:userId" element={<UserProfilePage />} />
                </Routes>
            </div>
        </BrowserRouter>
    );
}

export default App;
