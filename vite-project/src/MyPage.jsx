import React, { useState, useEffect } from 'react';
import api from './api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

// ⭐ [추가] 그래프의 툴팁을 원하는 모양으로 만들기 위한 커스텀 컴포넌트
const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
        return (
            <div className="custom-tooltip" style={{
                backgroundColor: 'rgba(255, 255, 255, 0.8)',
                padding: '10px',
                border: '1px solid #ccc',
                borderRadius: '5px'
            }}>
                <p className="label">{`날짜 : ${label}`}</p>
                <p className="intro">{`MMR : ${payload[0].value}`}</p>
            </div>
        );
    }
    return null;
};


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
                            {/* ⭐ [수정] 기본 툴팁 대신 우리가 만든 커스텀 툴팁을 사용합니다. */}
                            <Tooltip content={<CustomTooltip />} />
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
