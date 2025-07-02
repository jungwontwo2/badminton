package Tanguri.demo.Home.Controller;

import Tanguri.demo.Home.Dto.MyPageDto;
import Tanguri.demo.Home.Service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public ResponseEntity<MyPageDto> getMyPageDate(Authentication authentication) {
        //현재 로그인한 사용자의 ID 추출
        long userId = Long.parseLong(authentication.getName());

        //MyPageService 호출하여 해당 사용자의 전적 및 MMR 데이터 조회
        MyPageDto myPageData = myPageService.getMyPageDte(userId);

        //성공 응답과 함께 데이터 반환
        return ResponseEntity.ok(myPageData);
    }
}
