package Tanguri.demo.Home.Controller;

import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.MatchRequestDto;
import Tanguri.demo.Home.Service.MmrService;
import Tanguri.demo.Home.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matches")
public class MatchController {

    private final MmrService mmrService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> recordMatch(Authentication authentication, @RequestBody MatchRequestDto matchRequestDto) {

        // 1. DTO에서 받은 ID를 사용하여 각 선수의 User 엔티티를 DB에서 조회
        User winner1 = userService.getUserById(matchRequestDto.getWinner1Id());
        User loser1 = userService.getUserById(matchRequestDto.getLoser1Id());

        // 2. 복식 경기인지, 단식 경기인지 확인
        User winner2 = (matchRequestDto.getWinner2Id() != null) ? userService.getUserById(matchRequestDto.getWinner2Id()) : null;
        User loser2 = (matchRequestDto.getLoser2Id() != null) ? userService.getUserById(matchRequestDto.getLoser2Id()) : null;

        // 3. 조회한 User 엔티티들과 점수 정보를 MmrService로 전달하여 MMR 처리 및 기록 결과 기록을 요청
        mmrService.recordMatchAndProcessMmr(
                winner1,winner2,
                loser1,loser2,
                matchRequestDto.getWinnerScore(),
                matchRequestDto.getLoserScore()
        );

        //성공적으로 처리되었음을 알리는 응답 반환
        return ResponseEntity.ok("Match recorded successfully");
    }
}
