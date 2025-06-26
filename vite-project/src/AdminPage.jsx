import React, { useState, useEffect } from 'react';
import api from './api';
import { Link} from "react-router-dom";


function AdminPage() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUnverifiedUsers = async () => {
            try {
                setLoading(true);
                const response = await api.get('/api/admin/users/unverified');
                setUsers(response.data);
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

    if (loading) return <div>사용자 목록을 불러오는 중...</div>;
    if (error) return <div>에러 발생: {error.response?.data?.message || error.message} (관리자 권한이 있는지 확인하세요)</div>;

    return (
        <div style={{ maxWidth: '800px', margin: 'auto' }}>
            {/* ⭐ [수정] : 제목과 버튼을 감싸는 헤더 컨테이너 추가 */}
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                marginBottom: '20px',
                borderBottom: '2px solid #eee',
                paddingBottom: '10px'
            }}>
                <h2 style={{ margin: 0 }}>미인증 사용자 목록</h2>
                <Link to="/" style={{
                    padding: '8px 15px',
                    textDecoration: 'none',
                    backgroundColor: '#6c757d',
                    color: 'white',
                    borderRadius: '5px'
                }}>
                    메인으로 돌아가기
                </Link>
            </div>
            {users.length === 0 ? (
                <p>인증을 기다리는 사용자가 없습니다.</p>
            ) : (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                    <tr>
                        <th>프로필</th>
                        <th>닉네임</th>
                        <th>가입일</th>
                        <th>인증 처리</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map(user => (
                        <tr key={user.id}>
                            <td><img src={user.profileImageUrl} alt="profile" width="50" style={{ borderRadius: '50%' }}/></td>
                            <td>{user.nickname}</td>
                            <td>{new Date(user.createdAt).toLocaleDateString()}</td>
                            <td>
                                <button onClick={() => handleVerify(user.id)}>인증</button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default AdminPage;