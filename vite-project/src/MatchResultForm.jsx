import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from './api'; // 중앙에서 관리되는 axios 인스턴스

function MatchResultForm() {
    // ✅ [1. 상태 선언]
    const [users, setUsers] = useState([]);
    const [winners, setWinners] = useState([null, null]);
    const [losers, setLosers] = useState([null, null]);
    const [winnerScore, setWinnerScore] = useState(25);
    const [loserScore, setLoserScore] = useState('');
    const [isDoubles, setIsDoubles] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false); // 로딩 상태 추가
    const navigate = useNavigate();

    // ✅ [2. 부수 효과]
    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await api.get('/api/users');
                setUsers(response.data);
            } catch (error) {
                console.error("사용자 목록을 불러오는 데 실패했습니다.", error);
            }
        };
        fetchUsers();
    }, []);

    // ✅ [3. 이벤트 핸들러]
    const handlePlayerChange = (team, index, value) => {
        const userId = value ? parseInt(value, 10) : null;
        if (team === 'winners') {
            const newWinners = [...winners];
            newWinners[index] = userId;
            setWinners(newWinners);
        } else {
            const newLosers = [...losers];
            newLosers[index] = userId;
            setLosers(newLosers);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true); // 제출 시작 시 로딩 상태로 변경

        const playersInGame = isDoubles
            ? [winners[0], winners[1], losers[0], losers[1]]
            : [winners[0], losers[0]];

        const validPlayers = playersInGame.filter(p => p !== null);
        const uniquePlayers = new Set(validPlayers);

        if (validPlayers.length !== uniquePlayers.size) {
            alert('한 경기에 동일한 선수를 중복해서 선택할 수 없습니다.');
            setIsSubmitting(false); // 로딩 상태 해제
            return;
        }

        const matchData = {
            winner1Id: winners[0],
            winner2Id: isDoubles ? winners[1] : null,
            loser1Id: losers[0],
            loser2Id: isDoubles ? losers[1] : null,
            winnerScore: parseInt(winnerScore, 10),
            loserScore: parseInt(loserScore, 10),
        };

        try {
            await api.post('/api/matches', matchData);
            alert('경기 결과가 성공적으로 등록되었습니다. 상대방의 확인을 기다립니다.');
            navigate('/mypage');
        } catch (err) {
            if (err.response?.data?.message) {
                alert(err.response.data.message);
            } else {
                alert('경기 결과 등록에 실패했습니다.');
                console.error(err);
            }
        } finally {
            setIsSubmitting(false); // 성공/실패 여부와 관계없이 로딩 상태 해제
        }
    };

    // ✅ [4. 렌더링 준비]
    const selectedPlayerIds = new Set([
        ...winners.filter(id => id !== null),
        ...losers.filter(id => id !== null)
    ]);

    const renderPlayerOptions = (currentValue) => {
        return users.map(user => {
            const isDisabled = selectedPlayerIds.has(user.id) && user.id !== currentValue;
            return <option key={user.id} value={user.id} disabled={isDisabled}>{user.nickname}</option>;
        });
    };

    // ✅ [5. 화면 렌더링] - 스타일이 적용된 JSX
    // 스타일 객체들을 정의하여 가독성을 높입니다.
    const styles = {
        container: {
            maxWidth: '700px',
            margin: '40px auto',
            padding: '40px',
            backgroundColor: '#ffffff',
            borderRadius: '12px',
            boxShadow: '0 10px 25px rgba(0, 0, 0, 0.1)',
            fontFamily: "'Inter', sans-serif"
        },
        title: {
            textAlign: 'center',
            color: '#1a202c',
            fontSize: '2rem',
            marginBottom: '30px',
            fontWeight: 'bold',
        },
        form: {
            display: 'flex',
            flexDirection: 'column',
            gap: '30px'
        },
        radioGroup: {
            display: 'flex',
            justifyContent: 'center',
            gap: '10px',
            marginBottom: '10px'
        },
        radioLabel: {
            padding: '10px 20px',
            border: '1px solid #e2e8f0',
            borderRadius: '8px',
            cursor: 'pointer',
            transition: 'all 0.2s ease-in-out',
            backgroundColor: '#f7fafc'
        },
        radioInput: {
            display: 'none'
        },
        fieldset: {
            padding: '20px',
            borderRadius: '8px',
            border: '2px solid'
        },
        legend: {
            padding: '0 10px',
            fontWeight: '600',
            fontSize: '1.1rem'
        },
        playerSelectGroup: {
            display: 'grid',
            gridTemplateColumns: isDoubles ? '1fr 1fr 0.5fr' : '1fr 0.5fr',
            gap: '15px',
            alignItems: 'center'
        },
        select: {
            width: '100%',
            padding: '12px',
            border: '1px solid #cbd5e0',
            borderRadius: '6px',
            fontSize: '1rem',
            backgroundColor: '#fff'
        },
        input: {
            width: '100%',
            padding: '12px',
            border: '1px solid #cbd5e0',
            borderRadius: '6px',
            fontSize: '1rem',
            textAlign: 'center'
        },
        submitButton: {
            padding: '15px 20px',
            border: 'none',
            borderRadius: '8px',
            backgroundColor: isSubmitting ? '#a0aec0' : '#2c5282',
            color: 'white',
            fontSize: '1.1rem',
            fontWeight: 'bold',
            cursor: 'pointer',
            transition: 'background-color 0.2s',
        }
    };

    return (
        <div style={styles.container}>
            <h2 style={styles.title}>경기 결과 등록 🏸</h2>
            <form onSubmit={handleSubmit} style={styles.form}>
                <div style={styles.radioGroup}>
                    <label style={{...styles.radioLabel, backgroundColor: !isDoubles ? '#edf2f7' : '#f7fafc', color: !isDoubles ? '#2c5282' : '#4a5568'}}>
                        <input style={styles.radioInput} type="radio" name="gameType" checked={!isDoubles} onChange={() => setIsDoubles(false)} /> 단식
                    </label>
                    <label style={{...styles.radioLabel, backgroundColor: isDoubles ? '#edf2f7' : '#f7fafc', color: isDoubles ? '#2c5282' : '#4a5568'}}>
                        <input style={styles.radioInput} type="radio" name="gameType" checked={isDoubles} onChange={() => setIsDoubles(true)} /> 복식
                    </label>
                </div>

                <fieldset style={{...styles.fieldset, borderColor: '#38a169'}}>
                    <legend style={{...styles.legend, color: '#38a169'}}>🏆 승리팀</legend>
                    <div style={styles.playerSelectGroup}>
                        <select style={styles.select} value={winners[0] || ''} onChange={(e) => handlePlayerChange('winners', 0, e.target.value)} required>
                            <option value="">승자 1 선택</option>
                            {renderPlayerOptions(winners[0])}
                        </select>
                        {isDoubles && (
                            <select style={styles.select} value={winners[1] || ''} onChange={(e) => handlePlayerChange('winners', 1, e.target.value)} required>
                                <option value="">승자 2 선택</option>
                                {renderPlayerOptions(winners[1])}
                            </select>
                        )}
                        <input style={styles.input} type="number" value={winnerScore} onChange={(e) => setWinnerScore(e.target.value)} required />
                    </div>
                </fieldset>

                <fieldset style={{...styles.fieldset, borderColor: '#e53e3e'}}>
                    <legend style={{...styles.legend, color: '#e53e3e'}}>💔 패배팀</legend>
                    <div style={styles.playerSelectGroup}>
                        <select style={styles.select} value={losers[0] || ''} onChange={(e) => handlePlayerChange('losers', 0, e.target.value)} required>
                            <option value="">패자 1 선택</option>
                            {renderPlayerOptions(losers[0])}
                        </select>
                        {isDoubles && (
                            <select style={styles.select} value={losers[1] || ''} onChange={(e) => handlePlayerChange('losers', 1, e.target.value)} required>
                                <option value="">패자 2 선택</option>
                                {renderPlayerOptions(losers[1])}
                            </select>
                        )}
                        <input style={styles.input} type="number" value={loserScore} onChange={(e) => setLoserScore(e.target.value)} required />
                    </div>
                </fieldset>

                <button type="submit" style={styles.submitButton} disabled={isSubmitting}>
                    {isSubmitting ? '등록 중...' : '결과 등록하기'}
                </button>
            </form>
        </div>
    );
}

export default MatchResultForm;

