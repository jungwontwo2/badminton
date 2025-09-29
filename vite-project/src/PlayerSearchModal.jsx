import React, { useState, useEffect,useCallback } from 'react';
import api from './api';

// 스타일 객체
const styles = {
    modalOverlay: { position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0, 0, 0, 0.6)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 },
    modalContent: { background: 'white', padding: '30px', borderRadius: '10px', width: '90%', maxWidth: '500px', boxShadow: '0 5px 15px rgba(0,0,0,0.3)' },
    searchInputContainer: { display: 'flex', gap: '10px', marginBottom: '20px' },
    input: { flex: 1, padding: '10px', border: '1px solid #ccc', borderRadius: '5px' },
    searchButton: { padding: '10px 15px', border: 'none', backgroundColor: '#2c5282', color: 'white', borderRadius: '5px', cursor: 'pointer' },
    resultsList: { listStyle: 'none', padding: 0, margin: 0, maxHeight: '300px', overflowY: 'auto' },
    resultItem: { padding: '12px', borderBottom: '1px solid #eee', cursor: 'pointer' },
    closeButton: { marginTop: '20px', padding: '10px 15px', border: '1px solid #ccc', background: '#f7f7f7', borderRadius: '5px', cursor: 'pointer', float: 'right' }
};

function PlayerSearchModal({ onSelect, onClose, selectedPlayerIds }) {
    const [nickname, setNickname] = useState('');
    const [club, setClub] = useState('');
    const [results, setResults] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    // ✅ [수정] API 요청 로직을 useCallback으로 감싸 불필요한 재실행을 방지합니다.
    const performSearch = useCallback(async (searchNickname, searchClub) => {
        setIsLoading(true);
        try {
            const response = await api.get('/api/users/search', { params: { nickname: searchNickname, club: searchClub } });
            setResults(response.data);
        } catch (error) {
            console.error("검색 실패:", error);
            alert('검색 중 오류가 발생했습니다.');
        } finally {
            setIsLoading(false);
        }
    }, []); // 의존성 배열이 비어있으므로 이 함수는 한 번만 생성됩니다.

    // 팝업이 처음 열릴 때 기본 사용자 목록을 불러옵니다.
    useEffect(() => {
        performSearch('', ''); // 빈 검색어로 초기 검색을 실행합니다.
    }, [performSearch]);

    // '검색' 버튼 클릭 시 실행될 함수
    const handleSearchClick = () => {
        performSearch(nickname, club);
    };

    // ✅ [추가] 키보드 입력(특히 엔터)을 처리하는 함수
    const handleKeyDown = (e) => {
        // 만약 누른 키가 'Enter'라면, 검색을 실행합니다.
        if (e.key === 'Enter') {
            handleSearchClick();
        }
    };

    return (
        <div style={styles.modalOverlay} onClick={onClose}>
            <div style={styles.modalContent} onClick={e => e.stopPropagation()}>
                <h3>선수 검색</h3>
                <div style={styles.searchInputContainer}>
                    <input style={styles.input} type="text" value={nickname} onChange={e => setNickname(e.target.value)} onKeyDown={handleKeyDown} placeholder="닉네임" />
                    <input style={styles.input} type="text" value={club} onChange={e => setClub(e.target.value)} onKeyDown={handleKeyDown} placeholder="클럽명" />
                    <button style={styles.searchButton} onClick={handleSearchClick} disabled={isLoading}>
                        {isLoading ? '검색중...' : '검색'}
                    </button>
                </div>
                <ul style={styles.resultsList}>
                    {results.map(user => {
                        const isSelected = selectedPlayerIds.has(user.id);
                        return (
                            <li key={user.id}
                                onClick={() => !isSelected && onSelect(user)}
                                style={{
                                    ...styles.resultItem,
                                    cursor: isSelected ? 'not-allowed' : 'pointer',
                                    backgroundColor: isSelected ? '#f1f5f9' : 'transparent',
                                    color: isSelected ? '#94a3b8' : '#1a202c',
                                    fontWeight: isSelected ? 'bold' : 'normal'
                                }}>
                                {user.nickname} ({user.club} / {user.gradeGu})
                            </li>
                        );
                    })}
                </ul>
                <button style={styles.closeButton} onClick={onClose}>닫기</button>
            </div>
        </div>
    );
}

export default PlayerSearchModal;
