package Tanguri.demo.Home.Repository;

import Tanguri.demo.Home.Domain.MmrChangeLog;
import Tanguri.demo.Home.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//MMR 변경이 이루어진 로그들을 담는 리포지토리
public interface MmrChangeLogRepository extends JpaRepository<MmrChangeLog, Long> {

    //특정 사용자의 모든 MMR 변동 기록을 시간순(오름차순)으로 조회
    List<MmrChangeLog> findByUserOrderByLogDateAsc(User user);
}
