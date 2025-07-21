import React, { useState, useEffect, createContext, useContext } from 'react';
import { BrowserRouter, Routes, Route, Link, Outlet } from 'react-router-dom';
import axios from 'axios';

// 페이지 컴포넌트들을 import 합니다.
import KakaoLogin from './KakaoLogin';
import AuthRedirectHandler from './AuthRedirectHandler';
import ProfileForm from './ProfileForm';
import AdminPage from './AdminPage';
import MatchResultForm from './MatchResultForm';
import RankingPage from './RankingPage';
import MyPage from './MyPage';
import UserProfilePage from './UserProfilePage';

// ✅ [1단계] 로그인 상태를 전역으로 관리하기 위한 Context 생성
const AuthContext = createContext(null);

// ✅ [2단계] API 요청을 중앙에서 관리하고, 토큰 갱신 및 캐시 문제를 처리할 axios 인스턴스 생성
const api = axios.create({
    baseURL: 'http://localhost:8081', // 모든 요청의 기본 주소를 백엔드 서버로 설정합니다.
});

// 요청 인터셉터: 모든 요청에 토큰과 캐시 방지 헤더를 자동으로 추가합니다.
api.interceptors.request.use(
    (config) => {
        // 'accessToken'이라는 이름으로 저장된 토큰을 사용합니다.
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        config.headers['Cache-Control'] = 'no-cache';
        return config;
    },
    (error) => Promise.reject(error)
);

// 응답 인터셉터: API 응답이 401 에러(토큰 만료 등)일 때, 자동으로 토큰을 갱신하고 다시 요청합니다.
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const refreshToken = localStorage.getItem('refreshToken');
                const res = await axios.post('http://localhost:8081/auth/refresh', { refreshToken });
                const newAccessToken = res.data.accessToken;

                localStorage.setItem('accessToken', newAccessToken);

                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                return api(originalRequest);
            } catch (refreshError) {
                console.error('토큰 갱신 실패:', refreshError);
                // 토큰 갱신 실패 시 저장된 토큰들을 삭제합니다.
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                // 필요하다면 로그인 페이지로 리디렉션합니다.
                // window.location.href = '/';
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);


function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Layout />}>
                        <Route index element={<MainContent />} />
                        <Route path="admin" element={<AdminPage />} />
                        <Route path="record-match" element={<MatchResultForm />} />
                        <Route path="rankings" element={<RankingPage />} />
                        <Route path="mypage" element={<MyPage />} />
                        <Route path="profiles/:userId" element={<UserProfilePage />} />
                    </Route>
                    <Route path="/auth/kakao/callback" element={<AuthRedirectHandler />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

// ✅ [3단계] 로그인 상태와 사용자 정보를 Context로 제공하는 컴포넌트
function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isInitializing, setIsInitializing] = useState(true);

    useEffect(() => {
        const checkLoginStatus = async () => {
            const token = localStorage.getItem('accessToken');
            if (token) {
                try {
                    // 백엔드에 토큰이 유효한지 확인하고 사용자 정보를 받아오는 API
                    const response = await api.get('/api/users/me');
                    setUser(response.data);
                    setIsLoggedIn(true);
                } catch (error) {
                    console.error("로그인 상태 복원 실패:", error);
                    localStorage.removeItem('accessToken');
                    localStorage.removeItem('refreshToken');
                    setUser(null);
                    setIsLoggedIn(false);
                }
            }
            setIsInitializing(false);
        };
        checkLoginStatus();
    }, []);

    const authContextValue = {
        user,
        setUser,
        isLoggedIn,
        setIsLoggedIn,
        api // api 인스턴스도 context를 통해 전달하여 다른 컴포넌트에서 사용할 수 있게 합니다.
    };

    if (isInitializing) {
        return <div>앱 로딩 중...</div>;
    }

    return (
        <AuthContext.Provider value={authContextValue}>
            {children}
        </AuthContext.Provider>
    );
}

// ✅ [4단계] 헤더와 페이지 컨텐츠를 포함하는 레이아웃 컴포넌트
function Layout() {
    return (
        <div style={{ padding: '20px', textAlign: 'center' }}>
            <Header />
            <main>
                <Outlet /> {/* 라우팅되는 페이지 컴포넌트가 여기에 렌더링됩니다. */}
            </main>
        </div>
    );
}

function Header() {
    const { isLoggedIn, user, setUser, setIsLoggedIn } = useContext(AuthContext);

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setUser(null);
        setIsLoggedIn(false);
        // 필요하다면 메인 페이지로 이동
        // navigate('/');
    };

    return (
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
                {isLoggedIn && (
                    <>
                        <Link to="/mypage" style={{ marginRight: '20px' }}>내 정보</Link>
                        {user?.status === 'VERIFIED' && (
                            <Link to="/record-match" style={{ marginRight: '20px' }}>경기 결과 등록</Link>
                        )}
                        {user?.role === 'ADMIN' && (
                            <Link to="/admin" style={{ marginRight: '20px' }}>관리자 페이지</Link>
                        )}
                        <button onClick={handleLogout}>로그아웃</button>
                    </>
                )}
            </nav>
        </header>
    );
}

function MainContent() {
    const { isLoggedIn, user, setUser } = useContext(AuthContext);

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

    if (!isLoggedIn || !user) {
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
        </div>
    );
}

export default App;
