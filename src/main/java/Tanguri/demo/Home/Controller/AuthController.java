package Tanguri.demo.Home.Controller;


import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.AuthResponseDto;
import Tanguri.demo.Home.Dto.RefreshTokenRequestDto;
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
        String accessTokenFromKakao = authService.getAccessToken(code);
        // 2. '액세스 토큰'을 이용해 카카오로부터 '사용자 정보'를 받는다.
        User kakaoUser = authService.getUserInfo(accessTokenFromKakao);
        // 3. 받은 사용자 정보로 우리 서비스에 로그인/회원가입 처리한다.
        User user = authService.loginOrRegister(kakaoUser);

        //Access Token과 Refresh Token을 모두 생성하고 가져옴
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = user.getRefreshToken();


        // ⭐ [수정] : 토큰과 사용자 정보를 함께 DTO에 담아 반환
        return ResponseEntity.ok(new AuthResponseDto(accessToken,refreshToken, user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@RequestBody RefreshTokenRequestDto requestDto){
        String newAccessToken = authService.refreshAccessToken(requestDto.getRefreshToken());
        //새로운 Access Token만 담아서 보냄 (Refresh Token은 그대로 유지)
        return ResponseEntity.ok(new AuthResponseDto(newAccessToken, null, null));
    }
}
