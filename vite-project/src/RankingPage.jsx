import React, { useState, useEffect } from 'react';
import api from './api';

function RankingPage() {
    const [rankings, setRankings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchRankings = async () => {
            try {
                setLoading(true);
                const response = await api.get('/api/rankings');
                setRankings(response.data);
            } catch (err) {
                setError(err);
            }
            setLoading(false);
        };

        fetchRankings();
    }, []); // 컴포넌트가 처음 렌더링될 때 한 번만 실행

    if (loading) return <div>랭킹 정보를 불러오는 중...</div>;
    if (error) return <div>에러 발생: {error.message}</div>;

    const thStyle = { padding: '12px', borderBottom: '2px solid #dee2e6', backgroundColor: '#f8f9fa' };
    const tdStyle = { padding: '12px', borderBottom: '1px solid #eee', verticalAlign: 'middle' };

    return (
        <div style={{ maxWidth: '800px', margin: 'auto' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h2 style={{ margin: 0 }}>전체 랭킹</h2>
            </div>

            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'center' }}>
                <thead>
                <tr>
                    <th style={{ ...thStyle, width: '10%' }}>순위</th>
                    <th style={{ ...thStyle, width: '40%', textAlign: 'left' }}>선수</th>
                    <th style={{ ...thStyle, width: '30%' }}>클럽</th>
                    <th style={{ ...thStyle, width: '20%' }}>MMR</th>
                </tr>
                </thead>
                <tbody>
                {rankings.map((player, index) => (
                    <tr key={player.userId}>
                        <td style={tdStyle}>
                            <span style={{ fontWeight: 'bold', fontSize: '1.2em' }}>{index + 1}</span>
                        </td>
                        <td style={{ ...tdStyle, textAlign: 'left', display: 'flex', alignItems: 'center' }}>
                            <img src={player.profileImageUrl} alt={player.nickname} style={{ width: '40px', height: '40px', borderRadius: '50%', marginRight: '15px' }} />
                            <div>
                                <div>{player.nickname}</div>
                                <div style={{ fontSize: '0.8em', color: '#6c757d' }}>{player.gradeGu}</div>
                            </div>
                        </td>
                        <td style={tdStyle}>{player.club}</td>
                        <td style={{ ...tdStyle, fontWeight: 'bold', color: '#0056b3' }}>{player.mmr}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default RankingPage;
