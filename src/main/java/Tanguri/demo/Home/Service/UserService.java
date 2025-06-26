package Tanguri.demo.Home.Service;


import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Domain.UserStatus;
import Tanguri.demo.Home.Dto.ProfileUpdateDto;
import Tanguri.demo.Home.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public User updateProfile(Long userId, ProfileUpdateDto profileUpdateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id :" + userId));

        user.updateAdditionalInfo(
            profileUpdateDto.getClub(),
            profileUpdateDto.getAgeGroup(),
            profileUpdateDto.getGradeGu(),
            profileUpdateDto.getGradeSi(),
            profileUpdateDto.getGradeNational()
        );
        return user;
    }

    // 미인증(UNVERIFIED) 상태인 모든 사용자 조회
    @Transactional(readOnly = true)
    public List<User> getUnverifiedUsers(){
        return userRepository.findByStatus(UserStatus.UNVERIFIED);
    }

    // 사용자를 인증(VERIFIED) 상태로 변경
    @Transactional
    public User verifyUser(Long userId) {
        User user = getUserById(userId);
        user.verifyUser();
        return user;
    }
}
