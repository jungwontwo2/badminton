package Tanguri.demo.Home.Controller;

import Tanguri.demo.Home.Dto.RankingDto;
import Tanguri.demo.Home.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingController {

    private final UserService userService;

    //랭킹 조회
    @GetMapping
    public ResponseEntity<List<RankingDto>> getRankings() {
        List<RankingDto> rankings = userService.getRanking();
        return ResponseEntity.ok(rankings);
    }
}
