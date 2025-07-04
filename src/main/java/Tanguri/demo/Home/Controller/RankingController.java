package Tanguri.demo.Home.Controller;

import Tanguri.demo.Home.Dto.RankingDto;
import Tanguri.demo.Home.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingController {

    private final UserService userService;

    //닉네임 검색어를 받을 수 있도록 @RequestParam 추가
    @GetMapping
    public ResponseEntity<List<RankingDto>> getRankings(@RequestParam(value = "nickname",required = false) String nickname) {
        //서비스 호출 시 검색어를 그대로 전달
        List<RankingDto> rankings = userService.getRanking(nickname);
        return ResponseEntity.ok(rankings);
    }
}
