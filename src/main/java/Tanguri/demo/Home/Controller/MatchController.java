package Tanguri.demo.Home.Controller;

import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.AwaitingMatchDto;
import Tanguri.demo.Home.Dto.MatchRequestDto;
import Tanguri.demo.Home.Service.MmrService;
import Tanguri.demo.Home.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matches")
public class MatchController {

    private final MmrService mmrService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> recordMatch(Authentication authentication, @RequestBody MatchRequestDto matchRequestDto) {
        //현재 로그인한 사용자의 ID 가져옴
        long userId = Long.parseLong(authentication.getName());
        User registeredBy = userService.getUserById(userId);

        //서비스 호출 시 등록한 사용자(registeredBy)의 정보도 함께 전달
        mmrService.recordMatch(matchRequestDto,registeredBy);

        return ResponseEntity.ok("Match recorded successfully. Waiting for opponent's confirmation");
    }

    //상대방이 경기 결과를 '확인'처리
    @PatchMapping("/{matchId}/confirm-by-opponent")
    public ResponseEntity<String> confirmMatchByOpponent(Authentication authentication, @PathVariable Long matchId){
        //현재 로그인한 사용자의 ID 가져옴
        long opponentId = Long.parseLong(authentication.getName());
        User opponent = userService.getUserById(opponentId);

        //MmrService를 호출하여 상대방 확인 로직 수행
        mmrService.confirmMatchByOpponent(matchId,opponent);

        //성공 응답 반환
        return ResponseEntity.ok("상대방 확인이 완료되었습니다. 관리자 승인을 기다립니다.");
    }

    //내가 확인해야 할 경기 목록 조회
//    @GetMapping("/awaiting-my-confirmation")
//    public ResponseEntity<List<AwaitingMatchDto>> getAwaitingMatches(Authentication authentication){
//        long userId = Long.parseLong(authentication.getName());
//        User currnetUser = userService.getUserById(userId);
//        List<AwaitingMatchDto> awaitingMatches = mmrService.getMatchesAwaitingConfirmation(currnetUser);
//        return ResponseEntity.ok(awaitingMatches);
//    }

    // 내가 확인해야 할 경기 목록 조회
    @GetMapping("/awaiting-my-confirmation")
    public ResponseEntity<List<AwaitingMatchDto>> getAwaitingMyConfirmation(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        User currentUser = userService.getUserById(userId);
        List<AwaitingMatchDto> awaitingMatches = mmrService.getMatchesAwaitingMyConfirmation(currentUser);
        return ResponseEntity.ok(awaitingMatches);
    }

    // '내가 등록한 확인 대기 경기 목록'을 조회 (테스트용)
    @GetMapping("/awaiting-opponent-confirmation")
    public ResponseEntity<List<AwaitingMatchDto>> getAwaitingOpponentConfirmation(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        User currentUser = userService.getUserById(userId);
        List<AwaitingMatchDto> awaitingMatches = mmrService.getMatchesIRegisteredAndAwaiting(currentUser);
        return ResponseEntity.ok(awaitingMatches);
    }
}
