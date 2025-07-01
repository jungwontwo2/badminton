package Tanguri.demo.Home.Controller;

import Tanguri.demo.Home.Domain.MatchResult;
import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.PendingMatchDto;
import Tanguri.demo.Home.Service.MmrService;
import Tanguri.demo.Home.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final MmrService mmrService;

    //미인증 사용자 목록 조회
    @GetMapping("/users/unverified")
    public ResponseEntity<List<User>> getUnverifiedUsers(){
        List<User> users = userService.getUnverifiedUsers();
        return ResponseEntity.ok(users);
    }

    //사용자 인증완료 처리 API
    @PatchMapping("/users/{userId}/verify")
    public ResponseEntity<User> verifyUser(@PathVariable Long userId){
        User verifiedUser = userService.verifyUser(userId);
        return ResponseEntity.ok(verifiedUser);
    }

    //'승인 대기' 상태인 경기 목록들 조회하는 API
    @GetMapping("/matches/pending")
    public ResponseEntity<List<PendingMatchDto>> getPendingMatches(){
        List<PendingMatchDto> pendingMatches = mmrService.getPendingMatches();
        return ResponseEntity.ok(pendingMatches);
    }

    //특정 경기 결과를 '승인' 처리하는 API
    @PatchMapping("/matches/{matchId}/confirm")
    public ResponseEntity<String> confirmMatch(@PathVariable Long matchId){
        mmrService.confirmMatchAndProcessMmr(matchId);
        return ResponseEntity.ok("경기가 성공적으로 승인 처리되었습니다.");
    }
}
