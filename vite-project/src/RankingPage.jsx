import React, { useState, useEffect } from 'react';
import api from './api';

function RankingPage() {
    const [rankings, setRankings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    // ⭐ [추가] 사용자가 입력하는 검색어를 관리하는 상태
    const [searchTerm, setSearchTerm] = useState('');
    // ⭐ [추가] 실제 검색을 실행할 검색어를 관리하는 상태
    const [query, setQuery] = useState('');

    // ⭐ [수정] useEffect의 의존성 배열에 query를 추가합니다.
    useEffect(() => {
        const fetchRankings = async () => {
            try {
                setLoading(true);
                // ⭐ [수정] API 요청 시, 닉네임 검색어를 파라미터로 전달합니다.
                const response = await api.get('/api/rankings', {
                    params: { nickname: query }
                });
                setRankings(response.data);
            } catch (err) {
                setError(err);
            }
            setLoading(false);
        };

        fetchRankings();
    }, [query]); // query 값이 변경될 때마다 이 useEffect가 다시 실행됩니다.

    // ⭐ [추가] 검색 버튼 클릭 시 실행될 핸들러
    const handleSearch = () => {
        setQuery(searchTerm);
    };

    // ⭐ [추가] Enter 키를 눌렀을 때 검색이 실행되도록 하는 핸들러
    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    if (loading) return <div>랭킹 정보를 불러오는 중...</div>;
    if (error) return <div>에러 발생: {error.message}</div>;

    const thStyle = { padding: '12px', borderBottom: '2px solid #dee2e6', backgroundColor: '#f8f9fa' };
    const tdStyle = { padding: '12px', borderBottom: '1px solid #eee', verticalAlign: 'middle' };

    return (
        <div style={{ maxWidth: '800px', margin: 'auto' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h2 style={{ margin: 0 }}>전체 랭킹</h2>
            </div>

            {/* ⭐ [추가] 닉네임 검색 UI */}
            <div style={{ marginBottom: '20px', display: 'flex', gap: '10px' }}>
                <input
                    type="text"
                    placeholder="선수 닉네임으로 검색"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    onKeyPress={handleKeyPress}
                    style={{ flexGrow: 1, padding: '10px', fontSize: '16px' }}
                />
                <button onClick={handleSearch} style={{ padding: '10px 20px', fontSize: '16px' }}>검색</button>
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
