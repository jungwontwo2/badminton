package Tanguri.demo.Home.Repository;

import Tanguri.demo.Home.Domain.MatchResult;
import Tanguri.demo.Home.Domain.MatchStatus;
import Tanguri.demo.Home.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult,Long> {
    //특정 상태(status)의 모든 경기 결과를 찾는 쿼리 메서드
    List<MatchResult> findByStatus(MatchStatus status);

    //특정 사용자가 참여한 모든 확정된 경기 최신순으로 조회
    @Query("SELECT m From MatchResult m WHERE (m.winner1 = :user OR m.winner2 = :user OR m.loser1 = :user OR m.loser2 =:user) AND m.status ='CONFIRMED' ORDER BY m.matchDate DESC")
    List<MatchResult> findConfirmedMatchesByUser(@Param("user") User user);
}
