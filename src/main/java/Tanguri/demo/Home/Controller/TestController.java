package Tanguri.demo.Home.Controller;

import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.AuthResponseDto;
import Tanguri.demo.Home.Service.UserService;
import Tanguri.demo.Home.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Profile("dev")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/login/{userId}")
    @Transactional
    public ResponseEntity<AuthResponseDto> testLogin(@PathVariable Long userId){
        User user = userService.getUserById(userId);

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        user.updateRefreshToken(refreshToken);

        AuthResponseDto response = new AuthResponseDto(accessToken, refreshToken, user);
        return ResponseEntity.ok(response);
    }
}
