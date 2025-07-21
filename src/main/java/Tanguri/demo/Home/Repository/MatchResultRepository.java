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

    //내가 등록한 경기는 제외
    @Query("SELECT m From MatchResult m WHERE (m.winner1 = :user OR m.winner2 = :user OR m.loser1 = :user OR m.loser2 =:user) AND m.status ='AWAITING_OPPONENT' AND m.registeredBy != :user ORDER BY m.matchDate DESC")
    List<MatchResult> findMatchesAwaitingOpponentConfirmation(@Param("user") User user);

    //특정 사용자가 참여한 모든 '확정된' 경기를 최신순으로 조회하는 쿼리
    @Query("SELECT m FROM MatchResult m WHERE (m.winner1 = :user OR m.winner2 = :user OR m.loser1 = :user OR m.loser2 = :user) AND m.status = 'CONFIRMED' ORDER BY m.matchDate DESC")
    List<MatchResult> findConfirmedMatchesByUser(@Param("user") User user);

    //내가 등록했고, '상대방 확인 대기' 상태인 모든 경기를 조회하는 쿼리 (테스트용)
    List<MatchResult> findByRegisteredByAndStatus(User registeredBy, MatchStatus status);
}
