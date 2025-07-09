import React, { useState, useEffect } from 'react';
import api from './api';
import { useParams, Link } from 'react-router-dom'; // ⭐ [추가] URL 파라미터를 읽기 위한 useParams 훅
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

// 그래프의 툴팁을 원하는 모양으로 만들기 위한 커스텀 컴포넌트 (MyPage.jsx와 동일)
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

function UserProfilePage() {
    // ⭐ [추가] useParams 훅을 사용하여 URL에서 userId 값을 가져옵니다.
    const { userId } = useParams();

    const [profileData, setProfileData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUserProfileData = async () => {
            try {
                setLoading(true);
                // ⭐ [수정] API 주소를 /api/profiles/{userId} 로 변경합니다.
                const response = await api.get(`/api/profiles/${userId}`);
                setProfileData(response.data);
            } catch (err) {
                setError(err);
            }
            setLoading(false);
        };

        if (userId) {
            fetchUserProfileData();
        }
    }, [userId]); // userId 값이 변경될 때마다 이 useEffect가 다시 실행됩니다.

    if (loading) return <div>사용자 프로필을 불러오는 중...</div>;
    if (error) return <div>에러 발생: {error.message}</div>;
    if (!profileData) return <div>사용자 정보가 없습니다.</div>;

    // profileData 객체에서 userInfo, matchHistories, mmrPoints를 추출합니다.
    const { userInfo, matchHistories, mmrPoints } = profileData;

    const renderMmrChange = (change) => {
        if (change > 0) {
            return <span style={{ color: 'blue', fontWeight: 'bold' }}>▲ {change}</span>;
        } else if (change < 0) {
            return <span style={{ color: 'red', fontWeight: 'bold' }}>▼ {Math.abs(change)}</span>;
        }
        return <span>-</span>;
    };

    return (
        <div style={{ maxWidth: '900px', margin: 'auto' }}>
            {/* ⭐ [수정] 사용자 프로필 정보를 표시하는 섹션 */}
            <section style={{ textAlign: 'center', marginBottom: '30px' }}>
                <img src={userInfo.profileImageUrl} alt="profile" style={{ width: '120px', height: '120px', borderRadius: '50%' }} />
                <h2>{userInfo.nickname}</h2>
                <p>
                    {userInfo.club} / {userInfo.ageGroup} / MMR: <strong>{userInfo.mmr}</strong>
                </p>
                <p>
                    구: {userInfo.gradeGu} / 시: {userInfo.gradeSi} / 전국: {userInfo.gradeNational || '-'}
                </p>
            </section>

            {/* MMR 변동 그래프 섹션 (MyPage.jsx와 동일) */}
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

            {/* 경기 기록 섹션 (MyPage.jsx와 거의 동일) */}
            <section>
                <h3>경기 기록</h3>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'center' }}>
                    {/* ... (테이블 헤더는 동일) ... */}
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

export default UserProfilePage;
