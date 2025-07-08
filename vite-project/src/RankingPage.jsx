import React, { useState, useEffect } from 'react';
import api from './api';

function RankingPage() {
    const [rankings, setRankings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [searchParams, setSearchParams] = useState({
        nickname: '',
        club: '',
        ageGroup: '',
        grade: ''
    });

    const [query, setQuery] = useState({
        nickname: '',
        club: '',
        ageGroup: '',
        grade: ''
    });

    useEffect(() => {
        const fetchRankings = async () => {
            try {
                setLoading(true);
                const response = await api.get('/api/rankings', { params: query });
                setRankings(response.data);
            } catch (err) {
                setError(err);
            }
            setLoading(false);
        };
        fetchRankings();
    }, [query]);

    const handleParamChange = (e) => {
        const { name, value } = e.target;
        setSearchParams(prevParams => ({
            ...prevParams,
            [name]: value
        }));
    };

    const handleSearch = () => {
        setQuery(searchParams);
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    if (loading) return <div>랭킹 정보를 불러오는 중...</div>;
    if (error) return <div>에러 발생: {error.message}</div>;

    const thStyle = { padding: '12px', borderBottom: '2px solid #dee2e6', backgroundColor: '#f8f9fa' };
    const tdStyle = { padding: '12px', borderBottom: '1px solid #eee', verticalAlign: 'middle' };

    const ageGroupOptions = ["전체", "20대", "30대", "40대", "50대", "60대 이상"];
    const gradeOptions = ["전체", "A조", "B조", "C조", "D조", "초심"];

    return (
        <div style={{ maxWidth: '900px', margin: 'auto' }}>
            <h2 style={{ margin: 0, marginBottom: '20px', textAlign: 'center' }}>전체 랭킹</h2>

            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                gap: '10px',
                marginBottom: '20px',
                padding: '20px',
                border: '1px solid #eee',
                borderRadius: '8px'
            }}>
                <input type="text" name="nickname" placeholder="닉네임" value={searchParams.nickname} onChange={handleParamChange} onKeyPress={handleKeyPress} style={{ padding: '10px' }} />
                <input type="text" name="club" placeholder="클럽명" value={searchParams.club} onChange={handleParamChange} onKeyPress={handleKeyPress} style={{ padding: '10px' }} />

                <select name="ageGroup" value={searchParams.ageGroup} onChange={handleParamChange} style={{ padding: '10px' }}>
                    {ageGroupOptions.map(option => (
                        <option key={option} value={option === "전체" ? "" : option}>
                            {option}
                        </option>
                    ))}
                </select>

                <select name="grade" value={searchParams.grade} onChange={handleParamChange} style={{ padding: '10px' }}>
                    {gradeOptions.map(option => (
                        <option key={option} value={option === "전체" ? "" : option}>
                            {option}
                        </option>
                    ))}
                </select>

                <button onClick={handleSearch} style={{ padding: '10px 20px', gridColumn: '1 / -1', cursor: 'pointer' }}>검색</button>
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
                                {/* ⭐ [수정] 모든 급수 정보를 명확하게 표시합니다. */}
                                <div style={{ fontSize: '0.8em', color: '#6c757d' }}>
                                    {player.ageGroup} / 구:{player.gradeGu} / 시:{player.gradeSi} / 전국:{player.gradeNational || '-'}
                                </div>
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
