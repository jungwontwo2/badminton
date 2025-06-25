package Tanguri.demo.Home.Repository;

import Tanguri.demo.Home.Domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board,String> {
    Page<Board> findAll(Pageable pageable);

    // [새로 추가] BbsId를 기준으로 검색하고, 최신순으로 정렬하는 쿼리 메서드
    Page<Board> findByBbsId(String bbsId,Pageable pageable);
}
