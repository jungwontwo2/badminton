// src/BoardList.jsx

import React, { useState, useEffect } from 'react';
import axios from 'axios';
// ⭐ [수정 1] : react-router-dom에서 Link를 import합니다.
import { Link } from 'react-router-dom';

function BoardList({ bbsId, boardTitle }) {
    const [boards, setBoards] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchBoards = async () => {
            if (!bbsId) {
                setLoading(false);
                return;
            }
            try {
                setLoading(true);
                setError(null);

                const response = await axios.get(`http://localhost:8081/cop/bbs/${bbsId}/selectBoardList.do`, {
                    params: { page: currentPage, size: 10 }
                });

                setBoards(response.data.content || []);
                setTotalPages(response.data.totalPages || 0);
                setTotalElements(response.data.totalElements || 0);

            } catch (e) {
                setError(e);
            }
            setLoading(false);
        };

        fetchBoards();
    }, [bbsId, currentPage]);

    // --- (이하 핸들러, 텍스트 처리 함수들은 동일) ---
    const handlePrevPage = () => setCurrentPage(p => Math.max(p - 1, 0));
    const handleNextPage = () => setCurrentPage(p => Math.min(p + 1, totalPages - 1));

    const stripHtml = (html) => {
        if (!html) return '';
        let plainText = html.replace(/<[^>]*>?/gm, '');
        plainText = plainText.replace(/&[a-zA-Z0-9#]+;/g, ' ');
        return plainText;
    };

    const truncateText = (text, maxLength) => {
        const plainText = stripHtml(text);
        if (plainText.length > maxLength) {
            return plainText.substring(0, maxLength) + '...';
        }
        return plainText;
    };


    if (loading && boards.length === 0) {
        return <div>{boardTitle} 게시판 로딩 중...</div>;
    }

    if (error) return <div>에러 발생: {error.message}</div>;

    return (
        <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
            <h1 style={{ textAlign: 'center' }}>{boardTitle || '게시판'}</h1>

            <table style={{
                width: '100%',
                maxWidth: '1000px',
                margin: '20px auto',
                borderCollapse: 'collapse',
                boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                opacity: loading ? 0.5 : 1,
                transition: 'opacity 0.2s ease-in-out'
            }}>
                <thead>
                <tr style={{ backgroundColor: '#007bff', color: 'white' }}>
                    <th style={{ padding: '12px', border: '1px solid #ddd' }}>번호</th>
                    <th style={{ padding: '12px', border: '1px solid #ddd' }}>제목</th>
                    <th style={{ padding: '12px', border: '1px solid #ddd' }}>내용</th>
                    <th style={{ padding: '12px', border: '1px solid #ddd' }}>작성자</th>
                    <th style={{ padding: '12px', border: '1px solid #ddd' }}>작성일</th>
                </tr>
                </thead>
                <tbody>
                {boards.map((board, index) => (
                    <tr key={board.nttId} style={{
                        backgroundColor: index % 2 === 0 ? '#fff' : '#f8f9fa',
                        borderBottom: '1px solid #ddd'
                    }}>
                        <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>
                            {totalElements - (currentPage * 10) - index}
                        </td>
                        <td style={{ padding: '12px', border: '1px solid #ddd' }} title={stripHtml(board.title)}>
                            {/* ⭐ [핵심 수정] : <a> 태그를 <Link> 컴포넌트로 변경합니다. */}
                            <Link
                                to={`/article?NttId=${board.nttId}`}
                                style={{ textDecoration: 'none', color: '#0056b3' }}
                            >
                                {truncateText(board.title, 20)}
                            </Link>
                        </td>
                        <td style={{ padding: '12px', border: '1px solid #ddd' }} title={stripHtml(board.content)}>
                            {truncateText(board.content, 30)}
                        </td>
                        <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>{board.author}</td>
                        <td style={{ padding: '12px', border: '1px solid #ddd', textAlign: 'center' }}>{new Date(board.createdDate).toLocaleDateString()}</td>
                    </tr>
                ))}
                </tbody>
            </table>

            <div style={{ textAlign: 'center', marginTop: '20px' }}>
                <button onClick={handlePrevPage} disabled={currentPage === 0 || loading}>
                    이전
                </button>
                <span style={{ margin: '0 15px' }}>
                    Page {currentPage + 1} of {totalPages}
                </span>
                <button onClick={handleNextPage} disabled={currentPage + 1 >= totalPages || loading}>
                    다음
                </button>
            </div>
        </div>
    );
}

export default BoardList;
