package Tanguri.demo.Home.Domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 우리 시스템의 기본 키

    private Long kakaoId; // 카카오가 제공하는 고유 ID

    private String nickname;

    @Column(length = 500) // 프로필 이미지 URL은 길 수 있으므로 길이를 늘려줍니다.
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Role role; // 역할 (USER, ADMIN)

    @Enumerated(EnumType.STRING)
    private UserStatus status; // 계정 상태 (VERIFIED, UNVERIFIED)


    @CreationTimestamp // JPA 엔티티가 생성되는 시점의 시간을 자동으로 기록합니다.
    @Column(updatable = false) // 이 필드는 생성된 후 수정되지 않도록 설정합니다.
    private LocalDateTime createdAt;

    private String club;   //클럽
    private String ageGroup; //나이대
    private String gradeGu;  //구 급수
    private String gradeSi;  //시 급수
    private String gradeNational;//전국 급수

    @Builder
    public User(Long kakaoId, String nickname, String profileImageUrl, Role role, UserStatus status) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.status = status;
    }

    // 프로필 정보 업데이트를 위한 메서드
    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    // 클럽, 나이대, 급수 입력하면 update하는 함수
    public void updateAdditionalInfo(String club, String ageGroup, String gradeGu, String gradeSi, String gradeNational) {
        this.club = club;
        this.ageGroup = ageGroup;
        this.gradeGu = gradeGu;
        this.gradeSi = gradeSi;
        this.gradeNational = gradeNational;
    }
}
