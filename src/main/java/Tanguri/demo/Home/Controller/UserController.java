package Tanguri.demo.Home.Controller;


import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.ProfileUpdateDto;
import Tanguri.demo.Home.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // ⭐ [추가] : 토큰으로 '나'의 정보를 조회하는 API
    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(Authentication authentication, @RequestBody ProfileUpdateDto profileUpdateDto) {
        long userId = Long.parseLong(authentication.getName());
        User updatedUser = userService.updateProfile(userId, profileUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }
}
