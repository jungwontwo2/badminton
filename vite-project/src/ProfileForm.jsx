// src/ProfileForm.jsx
import React, { useState } from 'react';
import api from './api';

function ProfileForm({ user, onProfileUpdate }) {
    const [club, setClub] = useState('');
    const [ageGroup, setAgeGroup] = useState('');
    // ⭐ [수정] : 급수 상태를 3개로 분리
    const [gradeGu, setGradeGu] = useState('');
    const [gradeSi, setGradeSi] = useState('');
    const [gradeNational, setGradeNational] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        // ⭐ [수정] : 서버로 보낼 데이터 구조 변경
        const profileData = { club, ageGroup, gradeGu, gradeSi, gradeNational };

        try {
            // const response = await axios.put(`http://localhost:8081/api/users/${user.id}/profile`, profileData);
            const response = await api.put(`/api/users/profile`, profileData);
            alert('프로필이 성공적으로 업데이트되었습니다.');
            onProfileUpdate(response.data);
        } catch (error) {
            console.error('프로필 업데이트 실패:', error);
            alert('프로필 업데이트에 실패했습니다.');
        }
    };

    return (
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px', maxWidth: '400px', margin: 'auto' }}>
            <h2>추가 정보 입력</h2>
            <p>MMR 시스템을 이용하려면 추가 정보가 필요합니다.</p>

            <input
                type="text" value={club} onChange={(e) => setClub(e.target.value)}
                placeholder="소속 클럽" required
                style={{ padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
            />
            <input
                type="text" value={ageGroup} onChange={(e) => setAgeGroup(e.target.value)}
                placeholder="연령대 (예: 30대)" required
                style={{ padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
            />
            {/* ⭐ [수정] : 급수 입력 필드를 3개로 변경 */}
            <input
                type="text" value={gradeGu} onChange={(e) => setGradeGu(e.target.value)}
                placeholder="구 급수 (예: A조, 초심)" required
                style={{ padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
            />
            <input
                type="text" value={gradeSi} onChange={(e) => setGradeSi(e.target.value)}
                placeholder="시 급수 (예: B조, D조)" required
                style={{ padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
            />
            <input
                type="text" value={gradeNational} onChange={(e) => setGradeNational(e.target.value)}
                placeholder="전국 급수 (해당 시 입력)"
                style={{ padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
            />
            <button type="submit" style={{ padding: '10px', borderRadius: '5px', border: 'none', backgroundColor: '#007bff', color: 'white', cursor: 'pointer' }}>
                저장하기
            </button>
        </form>
    );
}

export default ProfileForm;