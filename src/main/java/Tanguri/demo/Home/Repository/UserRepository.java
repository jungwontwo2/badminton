package Tanguri.demo.Home.Repository;

import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //카카오 ID로 사용자 찾는 쿼리 메서드
    Optional<User> findByKakaoId(Long kakaoId);


    //특정 상태의 모든 사용자를 찾는 쿼리 메서드
    List<User> findByStatus(UserStatus status);

    //인증된 사용자들을 MMR 점수가 높은 순으로 정렬하여 모두 조회
    List<User> findByStatusOrderByMmrDesc(UserStatus status);
}
