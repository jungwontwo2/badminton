import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from './api'; // 중앙에서 관리되는 axios 인스턴스
import PlayerSearchModal from "./PlayerSearchModal.jsx";

// ✅ [추가] 선택된 선수를 표시하는 컴포넌트
const PlayerDisplay = ({ player, onButtonClick, placeholder }) => (
    <div onClick={onButtonClick} style={{
        padding: '12px', border: '1px solid #cbd5e0', borderRadius: '6px',
        cursor: 'pointer', backgroundColor: player ? '#edf2f7' : '#fff',
        textAlign: 'center'
    }}>
        {player ? `${player.nickname} (${player.club})` : placeholder}
    </div>
);

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

    // ✅ [추가] 팝업창 상태 관리
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [currentPlayerSlot, setCurrentPlayerSlot] = useState(null); // { team: 'winners', index: 0 }

    const openModal = (team, index) => {
        setCurrentPlayerSlot({ team, index });
        setIsModalOpen(true);
    };

    const handlePlayerSelect = (user) => {
        if (currentPlayerSlot) {
            const { team, index } = currentPlayerSlot;
            if (team === 'winners') {
                const newWinners = [...winners];
                newWinners[index] = user;
                setWinners(newWinners);
            } else {
                const newLosers = [...losers];
                newLosers[index] = user;
                setLosers(newLosers);
            }
        }
        setIsModalOpen(false);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 모든 선수 슬롯이 채워졌는지 확인
        const requiredPlayers = isDoubles ? 4 : 2;
        const selectedPlayers = [...winners, ...losers].filter(p => p !== null);
        if (isDoubles && selectedPlayers.length < 4) {
            alert('복식 경기는 4명의 선수를 모두 선택해야 합니다.');
            return;
        }
        if(!isDoubles && selectedPlayers.length < 2) {
            alert('단식 경기는 2명의 선수를 모두 선택해야 합니다.');
            return;
        }


        // 중복 선수 확인
        const selectedPlayerIds = selectedPlayers.map(p => p.id);
        if (new Set(selectedPlayerIds).size !== selectedPlayerIds.length) {
            alert('한 경기에 동일한 선수를 중복해서 등록할 수 없습니다.');
            return;
        }

        setIsSubmitting(true);

        const matchData = {
            winner1Id: winners[0]?.id,
            winner2Id: isDoubles ? winners[1]?.id : null,
            loser1Id: losers[0]?.id,
            loser2Id: isDoubles ? losers[1]?.id : null,
            winnerScore: parseInt(winnerScore, 10),
            loserScore: parseInt(loserScore, 10),
        };

        try {
            await api.post('/api/matches', matchData);
            alert('경기 결과가 성공적으로 등록되었습니다. 상대방의 확인을 기다립니다.');
            navigate('/mypage');
        } catch (err) {
            alert(err.response?.data?.message || '경기 결과 등록에 실패했습니다.');
            console.error(err);
        } finally {
            setIsSubmitting(false);
        }
    };

    const selectedPlayerIds = new Set(
        [...winners, ...losers].filter(p => p).map(p => p.id)
    );

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
        playerSelectGroup: { display: 'grid',
            gridTemplateColumns: isDoubles ? '1fr 1fr 80px' : '1fr 80px',
            gap: '15px',
            alignItems: 'center'
        },
        select: {
            width: '100%',
            padding: '12px',
            border: '1px solid #cbd5e0',
            borderRadius: '6px',
            fontSize: '1rsem',
            backgroundColor: '#fff'
        },
        input: {
            width: '100%',
            padding: '15px',
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
            {isModalOpen && (
                <PlayerSearchModal
                    onSelect={handlePlayerSelect}
                    onClose={() => setIsModalOpen(false)}
                    selectedPlayerIds={selectedPlayerIds}
                />
            )}

            <h2 style={styles.title}>경기 결과 등록 🏸</h2>
            <form onSubmit={handleSubmit} style={styles.form}>
                <div style={styles.radioGroup}>
                    <label style={{ ...styles.radioLabel, ...(isDoubles ? {} : { backgroundColor: '#2c5282', color: 'white', borderColor: '#2c5282' }) }}>
                        <input style={styles.radioInput} type="radio" name="matchType" checked={!isDoubles} onChange={() => setIsDoubles(false)} />
                        단식
                    </label>
                    <label style={{ ...styles.radioLabel, ...(isDoubles ? { backgroundColor: '#2c5282', color: 'white', borderColor: '#2c5282' } : {}) }}>
                        <input style={styles.radioInput} type="radio" name="matchType" checked={isDoubles} onChange={() => setIsDoubles(true)} />
                        복식
                    </label>
                </div>

                <fieldset style={{...styles.fieldset, borderColor: '#38a169'}}>
                    <legend style={{...styles.legend, color: '#38a169'}}>🏆 승리팀</legend>
                    <div style={styles.playerSelectGroup}>
                        <PlayerDisplay player={winners[0]} onButtonClick={() => openModal('winners', 0)} placeholder="승자 1 선택" />
                        {isDoubles && (
                            <PlayerDisplay player={winners[1]} onButtonClick={() => openModal('winners', 1)} placeholder="승자 2 선택" />
                        )}
                        <input style={styles.input} type="number" value={winnerScore} onChange={(e) => setWinnerScore(e.target.value)} required />
                    </div>
                </fieldset>

                <fieldset style={{...styles.fieldset, borderColor: '#e53e3e'}}>
                    <legend style={{...styles.legend, color: '#e53e3e'}}>💔 패배팀</legend>
                    <div style={styles.playerSelectGroup}>
                        <PlayerDisplay player={losers[0]} onButtonClick={() => openModal('losers', 0)} placeholder="패자 1 선택" />
                        {isDoubles && (
                            <PlayerDisplay player={losers[1]} onButtonClick={() => openModal('losers', 1)} placeholder="패자 2 선택" />
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

