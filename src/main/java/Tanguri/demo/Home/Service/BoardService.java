package Tanguri.demo.Home.Service;

import Tanguri.demo.Home.Domain.Board;
import Tanguri.demo.Home.Repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<Board> getAllBoards(){
// 1. Pageable 객체 생성
        // 첫 번째 인자: page (0부터 시작하므로 0이 첫 페이지)
        // 두 번째 인자: size (한 페이지에 보여줄 아이템 개수)
        // 세 번째 인자: sort (정렬 방식)
        Pageable topTen = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "frstRegistPnttm"));

        // 2. Repository에 Pageable 객체를 전달하여 호출
        Page<Board> boardPage = boardRepository.findAll(topTen);

        // 3. Page<Board>에서 실제 데이터인 List<Board>를 꺼내서 반환
        return boardPage.getContent();
    }

    @Transactional(readOnly = true)
    public Page<Board> getBoardsByBbsId(String bbsId,Pageable pageable){
        return boardRepository.findByBbsId(bbsId,pageable);
    }

    // [새로 추가] nttId로 단일 게시글을 조회하는 메서드
    @Transactional(readOnly = true)
    public Board getBoardById(String nttId) {
        // JpaRepository의 findById 메서드를 사용합니다.
        // 결과는 Optional<Board> 타입이므로, 결과가 없을 경우 예외를 던지도록 처리합니다.
        return boardRepository.findById(nttId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + nttId));
    }
}
