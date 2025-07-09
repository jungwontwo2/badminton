package Tanguri.demo.Home.Controller;


import Tanguri.demo.Home.Dto.UserProfileDto;
import Tanguri.demo.Home.Service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable long userId) {
        //UserProfileService 호출하여 사용자의 프로필 데이터 가져옴
        UserProfileDto userProfileData = userProfileService.getUserProfileData(userId);

        return ResponseEntity.ok(userProfileData);
    }
}
