import React, { useState, useEffect } from 'react';
import Select from 'react-select'; // react-select 라이브러리를 import합니다.
import api from './api'; // 우리가 만든 axios 인스턴스
import { useNavigate } from 'react-router-dom';

function MatchResultForm() {
    // 1. 상태(State) 관리
    const [allPlayers, setAllPlayers] = useState([]); // 서버에서 불러온 전체 선수 목록
    const [isDoubles, setIsDoubles] = useState(true); // 복식/단식 모드 토글

    // 선택된 선수 정보
    const [winner1, setWinner1] = useState(null);
    const [winner2, setWinner2] = useState(null);
    const [loser1, setLoser1] = useState(null);
    const [loser2, setLoser2] = useState(null);

    // 점수 정보
    const [winnerScore, setWinnerScore] = useState(25);
    const [loserScore, setLoserScore] = useState(0);

    const navigate = useNavigate(); // 페이지 이동을 위한 훅

    // 2. 컴포넌트가 처음 렌더링될 때, 서버에서 인증된 사용자 목록을 불러옵니다.
    useEffect(() => {
        const fetchVerifiedUsers = async () => {
            try {
                const response = await api.get('/api/users');
                // react-select에서 사용 가능한 형식 { value: id, label: '닉네임 (클럽)' } 으로 변환
                const formattedPlayers = response.data.map(user => ({
                    value: user.id,
                    label: `${user.nickname} (${user.club || '소속 없음'})`
                }));
                setAllPlayers(formattedPlayers);
            } catch (error) {
                console.error("선수 목록을 불러오는데 실패했습니다.", error);
            }
        };
        fetchVerifiedUsers();
    }, []); // 빈 배열: 처음 한 번만 실행

    // 3. 경기 결과 제출 핸들러
    const handleSubmit = async (e) => {
        e.preventDefault();

        // 유효성 검사
        if (!winner1 || !loser1 || (isDoubles && (!winner2 || !loser2))) {
            alert("모든 선수를 선택해주세요.");
            return;
        }

        const matchData = {
            winner1Id: winner1.value,
            winner2Id: isDoubles ? winner2.value : null,
            loser1Id: loser1.value,
            loser2Id: isDoubles ? loser2.value : null,
            winnerScore,
            loserScore
        };

        console.log("서버로 전송할 데이터:", matchData);

        try {
            await api.post('/api/matches', matchData);
            alert("경기 결과가 성공적으로 등록되었습니다!");
            navigate('/'); // 성공 시 홈으로 이동
        } catch (error) {
            console.error("경기 결과 등록 실패:", error);
            alert("경기 결과 등록에 실패했습니다.");
        }
    };

    // 4. JSX 렌더링
    return (
        <div style={{ maxWidth: '600px', margin: 'auto', padding: '20px' }}>
            <h2>경기 결과 입력</h2>
            <form onSubmit={handleSubmit}>
                {/* 단식/복식 토글 */}
                <div>
                    <label>
                        <input type="checkbox" checked={isDoubles} onChange={() => setIsDoubles(!isDoubles)} />
                        복식 경기
                    </label>
                </div>

                {/* 승리팀 */}
                <fieldset style={{ margin: '20px 0', padding: '15px' }}>
                    <legend>승리팀</legend>
                    <Select placeholder="승리팀 선수 1 선택" options={allPlayers} value={winner1} onChange={setWinner1} isClearable />
                    {isDoubles && <Select placeholder="승리팀 선수 2 선택" options={allPlayers} value={winner2} onChange={setWinner2} isClearable style={{ marginTop: '10px' }} />}
                    <input type="number" value={winnerScore} onChange={e => setWinnerScore(parseInt(e.target.value, 10))} required style={{ marginTop: '10px', width: '100px' }} />
                </fieldset>

                {/* 패배팀 */}
                <fieldset style={{ margin: '20px 0', padding: '15px' }}>
                    <legend>패배팀</legend>
                    <Select placeholder="패배팀 선수 1 선택" options={allPlayers} value={loser1} onChange={setLoser1} isClearable />
                    {isDoubles && <Select placeholder="패배팀 선수 2 선택" options={allPlayers} value={loser2} onChange={setLoser2} isClearable style={{ marginTop: '10px' }} />}
                    <input type="number" value={loserScore} onChange={e => setLoserScore(parseInt(e.target.value, 10))} required style={{ marginTop: '10px', width: '100px' }} />
                </fieldset>

                <button type="submit" style={{ width: '100%', padding: '15px', fontSize: '16px', cursor: 'pointer' }}>
                    결과 등록하기
                </button>
            </form>
        </div>
    );
}

export default MatchResultForm;
