package Tanguri.demo.Home.Repository;

import Tanguri.demo.Home.Domain.MmrChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

//MMR 변경이 이루어진 로그들을 담는 리포지토리
public interface MmrChangeLogRepository extends JpaRepository<MmrChangeLog,Long> {
}
