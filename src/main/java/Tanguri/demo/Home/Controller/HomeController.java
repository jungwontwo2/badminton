package Tanguri.demo.Home.Controller;

import Tanguri.demo.Home.Dto.BoardDto;
import Tanguri.demo.Home.Domain.Board;
import Tanguri.demo.Home.Service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cop/bbs")
public class HomeController {

    private final BoardService boardService;

    public HomeController(BoardService boardService) {
        this.boardService = boardService;
    }


    @GetMapping("/{bbsId}/selectBoardList.do")
    public ResponseEntity<Page<BoardDto>> getBoardList(@PathVariable String bbsId,
   @PageableDefault(size = 10, sort = "frstRegistPnttm", direction = Sort.Direction.DESC) Pageable pageable) {
        // 1. 서비스에서 페이징 처리된 Board 페이지 객체를 가져옵니다.
        Page<Board> boardPage = boardService.getBoardsByBbsId(bbsId, pageable);

        // 2. Page<Board>를 Page<BoardDto>로 변환합니다.
        // Page 객체의 .map() 기능을 사용하는 것이 가장 중요합니다.
        // 이 기능은 내용물(content)은 DTO로 변환하면서 페이지 정보는 그대로 유지해줍니다.
        Page<BoardDto> boardDtoPage = boardPage.map(BoardDto::fromEntity);

        // 3. ⭐ 핵심: DTO로 변환된 'Page 객체' 자체를 반환합니다. ⭐
        // 절대로 .getContent()를 붙이면 안 됩니다!
        return ResponseEntity.ok(boardDtoPage);
    }

    // [새로 추가] 게시글 c상세 조회를 위한 메서드
    @GetMapping("/selectBoardArticle.do")
    public ResponseEntity<BoardDto> getBoardArticle(@RequestParam("NttId") String nttId) {
        // 1. 서비스에서 nttId에 해당하는 단일 Board 엔티티를 가져옵니다.
        Board board = boardService.getBoardById(nttId);

        // 2. Board 엔티티를 BoardDto로 변환합니다.
        BoardDto boardDto = BoardDto.fromEntity(board);

        // 3. 성공 응답(200 OK)과 함께 DTO를 body에 담아 반환합니다.
        return ResponseEntity.ok(boardDto);
    }
}
