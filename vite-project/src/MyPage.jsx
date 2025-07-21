import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

// API 요청을 중앙에서 관리하고, 토큰 갱신 및 캐시 문제를 처리할 axios 인스턴스
const api = axios.create({
    baseURL: 'http://localhost:8081',
});

// 요청 인터셉터: 모든 요청에 토큰과 캐시 방지 헤더를 자동으로 추가
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        config.headers['Cache-Control'] = 'no-cache';
        config.headers['Pragma'] = 'no-cache';
        config.headers['Expires'] = '0';
        return config;
    },
    (error) => Promise.reject(error)
);

// 응답 인터셉터: 401 에러 시 자동으로 토큰을 갱신하고 다시 요청
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
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);


const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
        return (
            <div className="custom-tooltip" style={{
                backgroundColor: 'rgba(255, 255, 255, 0.9)',
                padding: '10px',
                border: '1px solid #ccc',
                borderRadius: '5px',
                boxShadow: '2px 2px 5px rgba(0,0,0,0.1)'
            }}>
                <p className="label">{`날짜 : ${label}`}</p>
                <p className="intro" style={{ color: '#8884d8' }}>{`MMR : ${payload[0].value}`}</p>
            </div>
        );
    }
    return null;
};

function MyPage() {
    const [myData, setMyData] = useState(null);
    const [awaitingMyConf, setAwaitingMyConf] = useState([]);
    // ✅ [제거] 테스트용 상태를 제거합니다.
    // const [awaitingOpponentConf, setAwaitingOpponentConf] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // ✅ [수정] 이제 '내가 확인할 경기' 목록만 불러옵니다.
    const fetchListsData = async () => {
        try {
            const response = await api.get('/api/matches/awaiting-my-confirmation');
            setAwaitingMyConf(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            console.error("확인할 경기 목록 로딩 실패:", err);
        }
    };

    useEffect(() => {
        const fetchInitialData = async () => {
            try {
                setLoading(true);
                const myPageResponse = await api.get('/api/mypage');
                setMyData(myPageResponse.data);
                await fetchListsData();
            } catch (err) {
                console.error("내 정보 로딩 실패:", err);
                setError(err);
            }
            setLoading(false);
        };

        fetchInitialData();
    }, []);

    const handleConfirmMatch = async (matchId) => {
        if (!window.confirm("이 경기 결과를 [확인]하시겠습니까? 확인 후에는 관리자 승인 단계로 넘어갑니다.")) return;
        try {
            await api.patch(`/api/matches/${matchId}/confirm-by-opponent`);
            alert("경기 결과가 확인되었습니다. 관리자 승인을 기다립니다.");
            // 확인 성공 후, 목록을 다시 불러와 화면을 갱신합니다.
            await fetchListsData();
        } catch (err) {
            console.error("경기 확인 처리 실패:", err);
            alert("처리 중 오류가 발생했습니다: " + (err.response?.data?.message || err.message));
        }
    };

    if (loading) return <div style={{ textAlign: 'center', padding: '50px' }}>내 정보 페이지를 불러오는 중...</div>;
    if (error) return <div style={{ textAlign: 'center', padding: '50px', color: 'red' }}>에러 발생: {error.response?.data?.message || error.message}</div>;
    if (!myData) return <div style={{ textAlign: 'center', padding: '50px' }}>데이터가 없습니다.</div>;

    const { matchHistories, mmrPoints } = myData;

    const renderMmrChange = (change) => {
        if (change > 0) {
            return <span style={{ color: 'blue', fontWeight: 'bold' }}>▲ {change}</span>;
        } else if (change < 0) {
            return <span style={{ color: 'red', fontWeight: 'bold' }}>▼ {Math.abs(change)}</span>;
        }
        return <span>-</span>;
    };

    return (
        <div style={{ maxWidth: '900px', margin: 'auto', padding: '20px' }}>
            <h2>개인 전적 페이지</h2>

            <section style={{ marginBottom: '40px', padding: '20px', border: '1px solid #ffc107', borderRadius: '8px', backgroundColor: '#fff3cd' }}>
                <h3>내가 확인할 경기 목록</h3>
                {awaitingMyConf.length === 0 ? <p>확인할 경기가 없습니다.</p> : (
                    <div style={{ overflowX: 'auto' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'center' }}>
                            <thead>
                            <tr>
                                <th>등록자</th><th>경기</th><th>스코어</th><th>날짜</th><th>처리</th>
                            </tr>
                            </thead>
                            <tbody>
                            {awaitingMyConf.map(match => (
                                <tr key={match.matchId}>
                                    <td>{match.registeredByName}</td>
                                    <td>{match.winnerTeam} vs {match.loserTeam}</td>
                                    <td>{match.score}</td>
                                    <td>{new Date(match.matchDate).toLocaleDateString()}</td>
                                    <td><button onClick={() => handleConfirmMatch(match.matchId)}>확인</button></td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>

            {/* ✅ [제거] 테스트용 섹션을 완전히 제거했습니다. */}

            <section style={{ marginBottom: '40px' }}>
                <h3>MMR 변동 그래프</h3>
                <div style={{ width: '100%', height: '300px' }}>
                    <ResponsiveContainer>
                        <LineChart data={mmrPoints} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="date" />
                            <YAxis domain={['dataMin - 50', 'dataMax + 50']} />
                            <Tooltip content={<CustomTooltip />} />
                            <Legend />
                            <Line type="monotone" dataKey="mmr" name="MMR" stroke="#8884d8" activeDot={{ r: 8 }} />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </section>

            <section>
                <h3>경기 기록</h3>
                <div style={{ overflowX: 'auto' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'center' }}>
                        <thead>
                        <tr style={{ backgroundColor: '#f8f9fa' }}>
                            <th style={{ padding: '10px', border: '1px solid #ddd' }}>결과</th>
                            <th style={{ padding: '10px', border: '1px solid #ddd' }}>내 파트너</th>
                            <th style={{ padding: '10px', border: '1px solid #ddd' }}>상대팀</th>
                            <th style={{ padding: '10px', border: '1px solid #ddd' }}>스코어</th>
                            <th style={{ padding: '10px', border: '1px solid #ddd' }}>MMR 변동</th>
                            <th style={{ padding: '10px', border: '1px solid #ddd' }}>경기 날짜</th>
                        </tr>
                        </thead>
                        <tbody>
                        {matchHistories && matchHistories.length > 0 ? (
                            matchHistories.map(match => (
                                <tr key={match.matchId}>
                                    <td style={{ padding: '10px', border: '1px solid #ddd', fontWeight: 'bold', color: match.result === '승' ? 'blue' : 'red' }}>{match.result}</td>
                                    <td style={{ padding: '10px', border: '1px solid #ddd' }}>{match.myPartner}</td>
                                    <td style={{ padding: '10px', border: '1px solid #ddd' }}>{match.opponents}</td>
                                    <td style={{ padding: '10px', border: '1px solid #ddd' }}>{match.score}</td>
                                    <td style={{ padding: '10px', border: '1px solid #ddd' }}>{renderMmrChange(match.mmrChange)}</td>
                                    <td style={{ padding: '10px', border: '1px solid #ddd' }}>{new Date(match.matchDate).toLocaleDateString()}</td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="6" style={{ padding: '20px' }}>경기 기록이 없습니다.</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}

export default MyPage;
