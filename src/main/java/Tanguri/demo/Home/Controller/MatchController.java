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
        //컨트롤러는 이제 DTO를 서비스로 전달하는 역할만 함
        //선수 정보를 조회하고, 경기 결과를 '승인대기'상태로 저장하는 로직은
        //MmrService의 recordMatch 메서드가 책임짐
        mmrService.recordMatch(matchRequestDto);

        return ResponseEntity.ok("Match recorded successfully. Waiting for admin approval");
    }
}
