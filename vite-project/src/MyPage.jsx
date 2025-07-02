import React, { useState, useEffect } from 'react';
import api from './api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

function MyPage() {
    const [myData, setMyData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchMyPageData = async () => {
            try {
                setLoading(true);
                const response = await api.get('/api/mypage');
                setMyData(response.data);
            } catch (err) {
                setError(err);
            }
            setLoading(false);
        };

        fetchMyPageData();
    }, []);

    if (loading) return <div>내 정보 페이지를 불러오는 중...</div>;
    if (error) return <div>에러 발생: {error.message}</div>;
    if (!myData) return <div>데이터가 없습니다.</div>;

    const { matchHistories, mmrPoints } = myData;

    const renderMmrChange = (change) => {
        if (change > 0) {
            return <span style={{ color: 'blue' }}>▲ {change}</span>;
        } else if (change < 0) {
            return <span style={{ color: 'red' }}>▼ {Math.abs(change)}</span>;
        }
        return <span>-</span>;
    };

    return (
        <div style={{ maxWidth: '900px', margin: 'auto' }}>
            <h2>개인 전적 페이지</h2>

            {/* MMR 변동 그래프 섹션 */}
            <section style={{ marginBottom: '40px' }}>
                <h3>MMR 변동 그래프</h3>
                <div style={{ width: '100%', height: '300px' }}>
                    <ResponsiveContainer>
                        <LineChart data={mmrPoints} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="date" />
                            <YAxis domain={['dataMin - 50', 'dataMax + 50']} />
                            <Tooltip />
                            <Legend />
                            <Line type="monotone" dataKey="mmr" stroke="#8884d8" activeDot={{ r: 8 }} />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </section>

            {/* 경기 기록 섹션 */}
            <section>
                <h3>경기 기록</h3>
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
                    {matchHistories.map(match => (
                        <tr key={match.matchId}>
                            <td style={{ padding: '10px', border: '1px solid #ddd', fontWeight: 'bold', color: match.result === '승' ? 'blue' : 'red' }}>{match.result}</td>
                            <td style={{ padding: '10px', border: '1px solid #ddd' }}>{match.myPartner}</td>
                            <td style={{ padding: '10px', border: '1px solid #ddd' }}>{match.opponents}</td>
                            <td style={{ padding: '10px', border: '1px solid #ddd' }}>{match.score}</td>
                            <td style={{ padding: '10px', border: '1px solid #ddd' }}>{renderMmrChange(match.mmrChange)}</td>
                            <td style={{ padding: '10px', border: '1px solid #ddd' }}>{new Date(match.matchDate).toLocaleDateString()}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </section>
        </div>
    );
}

export default MyPage;
