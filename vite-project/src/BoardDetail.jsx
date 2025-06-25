// src/BoardDetail.jsx

import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import axios from 'axios';

function BoardDetail() {
    // URL의 쿼리 파라미터(?NttId=...)를 읽기 위한 훅
    const [searchParams] = useSearchParams();
    const nttId = searchParams.get('NttId'); // 'NttId' 파라미터 값 추출

    const [article, setArticle] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchArticle = async () => {
            if (!nttId) {
                setError(new Error("게시글 ID가 없습니다."));
                setLoading(false);
                return;
            }
            try {
                setLoading(true);
                setError(null);
                const response = await axios.get(`http://localhost:8081/cop/bbs/selectBoardArticle.do`, {
                    params: { NttId: nttId }
                });
                setArticle(response.data);
            } catch (e) {
                setError(e);
            }
            setLoading(false);
        };

        fetchArticle();
    }, [nttId]); // nttId가 변경될 때마다 데이터를 다시 불러옵니다.

    if (loading) return <div>게시글을 불러오는 중...</div>;
    if (error) return <div>에러: {error.message}</div>;
    if (!article) return <div>게시글 정보를 찾을 수 없습니다.</div>;

    // HTML 태그를 포함한 내용을 렌더링하기 위한 함수
    const createMarkup = (htmlContent) => {
        return { __html: htmlContent };
    };

    return (
        <div style={{ maxWidth: '800px', margin: '40px auto', padding: '20px', fontFamily: 'sans-serif' }}>
            <h1 style={{ borderBottom: '2px solid #eee', paddingBottom: '10px' }}>{article.title}</h1>
            <div style={{ display: 'flex', justifyContent: 'space-between', color: '#888', marginBottom: '20px' }}>
                <span>작성자: {article.author}</span>
                <span>작성일: {new Date(article.createdDate).toLocaleDateString()}</span>
            </div>

            {/* dangerouslySetInnerHTML:
              보안상 위험할 수 있으므로(XSS 공격) 사용에 주의가 필요합니다.
              서버에서 받은 HTML이 신뢰할 수 있는 경우에만 사용해야 합니다.
              여기서는 사용자가 입력한 서식을 그대로 보여주기 위해 사용합니다.
            */}
            <div
                dangerouslySetInnerHTML={createMarkup(article.content)}
                style={{ lineHeight: '1.6', minHeight: '200px', padding: '10px', border: '1px solid #ddd', borderRadius: '5px' }}
            />

            <div style={{ textAlign: 'center', marginTop: '30px' }}>
                <Link to="/" style={{
                    padding: '10px 20px',
                    backgroundColor: '#6c757d',
                    color: 'white',
                    textDecoration: 'none',
                    borderRadius: '5px'
                }}>
                    목록으로
                </Link>
            </div>
        </div>
    );
}

export default BoardDetail;
