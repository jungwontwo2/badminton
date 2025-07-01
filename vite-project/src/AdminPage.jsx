import React, { useState, useEffect } from 'react';
import api from './api';
import { Link} from "react-router-dom";


function AdminPage() {
    const [unverifiedUsers, setUnverifiedUsers] = useState([]);
    const [pendingMatches, setPendingMatches] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUnverifiedUsers = async () => {
            try {
                setLoading(true);
                const userResponse = await api.get('/api/admin/users/unverified');
                const matchResponse = await api.get('/api/admin/matches/pending');

                setUnverifiedUsers(userResponse.data);
                setPendingMatches(matchResponse.data);

            } catch (err) {
                setError(err);
            }
            setLoading(false);
        };
        fetchUnverifiedUsers();
    }, []);

    const handleVerify = async (userId) => {
        if (!window.confirm("이 사용자를 인증 처리하시겠습니까?")) {
            return;
        }
        try {
            await api.patch(`/api/admin/users/${userId}/verify`);
            // 인증 성공 시, 화면에서 해당 유저를 즉시 제거
            setUsers(currentUsers => currentUsers.filter(user => user.id !== userId));
            alert("성공적으로 인증 처리되었습니다.");
        } catch (err) {
            alert("인증 처리에 실패했습니다: " + err.message);
        }
    };

    //경기 결과를 승인하는 함수
    const handleConfirmMatch = async (matchId) => {
        if (!window.confirm("이 경기 결과를 승인하시겠습니까? MMR이 변동됩니다.")) return;
        try {
            await api.patch(`/api/admin/matches/${matchId}/confirm`);
            setPendingMatches(currentMatches => currentMatches.filter(match => match.id !== matchId));
            alert("경기 결과가 성공적으로 승인되었습니다.");
        } catch (err) {
            alert("경기 결과 승인에 실패했습니다: " + err.message);
        }
    };

    if (loading) return <div>사용자 목록을 불러오는 중...</div>;
    if (error) return <div>에러 발생: {error.response?.data?.message || error.message} (관리자 권한이 있는지 확인하세요)</div>;

    return (
        <div style={{ maxWidth: '1000px', margin: 'auto' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '2px solid #eee', paddingBottom: '10px' }}>
                <h2 style={{ margin: 0 }}>관리자 페이지</h2>
                <Link to="/" style={{ padding: '8px 15px', textDecoration: 'none', backgroundColor: '#6c757d', color: 'white', borderRadius: '5px' }}>
                    메인으로 돌아가기
                </Link>
            </div>

            {/* --- 미인증 사용자 목록 섹션 --- */}
            <section>
                <h3>미인증 사용자 목록</h3>
                {unverifiedUsers.length === 0 ? <p>인증을 기다리는 사용자가 없습니다.</p> : (
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'center' }}>
                        {/* ... thead ... */}
                        <tbody>
                        {unverifiedUsers.map(user => (
                            <tr key={user.id}>
                                <td><img src={user.profileImageUrl} alt="profile" width="50" style={{ borderRadius: '50%' }}/></td>
                                <td>{user.nickname}</td>
                                <td>{new Date(user.createdAt).toLocaleDateString()}</td>
                                <td><button onClick={() => handleVerifyUser(user.id)}>인증</button></td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </section>

            <hr style={{ margin: '40px 0' }} />

            {/* ⭐ [추가] 승인 대기 경기 목록 섹션 --- */}
            <section>
                <h3>승인 대기 경기 목록</h3>
                {pendingMatches.length === 0 ? <p>승인을 기다리는 경기 결과가 없습니다.</p> : (
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'center' }}>
                        <thead style={{ backgroundColor: '#f2f2f2' }}>
                        <tr>
                            <th style={{padding: '10px', border: '1px solid #ddd'}}>승리팀</th>
                            <th style={{padding: '10px', border: '1px solid #ddd'}}>패배팀</th>
                            <th style={{padding: '10px', border: '1px solid #ddd'}}>스코어</th>
                            <th style={{padding: '10px', border: '1px solid #ddd'}}>경기 날짜</th>
                            <th style={{padding: '10px', border: '1px solid #ddd'}}>승인 처리</th>
                        </tr>
                        </thead>
                        <tbody>
                        {/* ⭐ [수정] DTO의 필드 이름(예: winner1Name)으로 데이터를 표시합니다. */}
                        {pendingMatches.map(match => (
                            <tr key={match.matchId}>
                                <td>{match.winner1Name} {match.winner2Name && `/ ${match.winner2Name}`}</td>
                                <td>{match.loser1Name} {match.loser2Name && `/ ${match.loser2Name}`}</td>
                                <td>{match.winnerScore} : {match.loserScore}</td>
                                <td>{new Date(match.matchDate).toLocaleDateString()}</td>
                                <td><button onClick={() => handleConfirmMatch(match.matchId)}>승인</button></td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </section>
        </div>
    );
}

export default AdminPage;