package Tanguri.demo.Home.Controller;

import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    //미인증 사용자 목록 조회
    @GetMapping("/users/unverified")
    public ResponseEntity<List<User>> getUnverifiedUsers(){
        List<User> users = userService.getUnverifiedUsers();
        return ResponseEntity.ok(users);
    }

    //사용자 인증완료 처리 API
    @PatchMapping("/users/{userId}/verify")
    public ResponseEntity<User> verifyUser(@PathVariable Long userId){
        User verifiedUser = userService.verifyUser(userId);
        return ResponseEntity.ok(verifiedUser);
    }
}
