package Tanguri.demo.Home.Controller;


import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.ProfileUpdateDto;
import Tanguri.demo.Home.Dto.UserSearchDto;
import Tanguri.demo.Home.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<User>> getAllVerifiedUsers(){
        List<User> users = userService.getVerifiedUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchDto>> searchUsers(
            @RequestParam(value = "nickname",required = false) String nickname,
            @RequestParam(value = "club",required = false) String club){
        List<UserSearchDto> users = userService.searchUsersByCriteria(nickname, club);
        return ResponseEntity.ok(users);
    }


}
