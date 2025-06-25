package Tanguri.demo.Home.Service;


import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.ProfileUpdateDto;
import Tanguri.demo.Home.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
