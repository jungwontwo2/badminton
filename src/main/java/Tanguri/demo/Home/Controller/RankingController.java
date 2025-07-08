package Tanguri.demo.Home.Controller;

import Tanguri.demo.Home.Dto.RankingDto;
import Tanguri.demo.Home.Dto.SearchCond;
import Tanguri.demo.Home.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingController {

    private final UserService userService;

    //@ModelAttribute SearchCond 사용하도록 변경
    @GetMapping
    public ResponseEntity<List<RankingDto>> getRankings(@ModelAttribute SearchCond searchCond) {
        //서비스 호출 시 검색 조건이 담긴 DTO 객체 그대로 전달
        List<RankingDto> rankings = userService.getRanking(searchCond);
        return ResponseEntity.ok(rankings);
    }
}
