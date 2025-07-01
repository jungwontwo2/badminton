package Tanguri.demo.Home.Repository;

import Tanguri.demo.Home.Domain.MatchResult;
import Tanguri.demo.Home.Domain.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult,Long> {
    //특정 상태(status)의 모든 경기 결과를 찾는 쿼리 메서드
    List<MatchResult> findByStatus(MatchStatus status);


}
