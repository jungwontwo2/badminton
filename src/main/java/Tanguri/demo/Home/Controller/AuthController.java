package Tanguri.demo.Home.Controller;


import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.AuthResponseDto;
import Tanguri.demo.Home.Service.AuthService;
import Tanguri.demo.Home.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    //카카오 로그인/회원가입을 처리하는 엔드포인트
    @GetMapping("/kakao/callback")
    public ResponseEntity<AuthResponseDto> kakaoCallback(@RequestParam("code") String code) throws Exception {
        // 1. 받은 '인가 코드'를 이용해 카카오로부터 '액세스 토큰'을 받는다.
        String accessToken = authService.getAccessToken(code);
        // 2. '액세스 토큰'을 이용해 카카오로부터 '사용자 정보'를 받는다.
        User kakaoUser = authService.getUserInfo(accessToken);
        // 3. 받은 사용자 정보로 우리 서비스에 로그인/회원가입 처리한다.
        User user = authService.loginOrRegister(kakaoUser);

        // ⭐ [수정] : JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name());

        // ⭐ [수정] : 토큰과 사용자 정보를 함께 DTO에 담아 반환
        return ResponseEntity.ok(new AuthResponseDto(token, user));
    }

}
