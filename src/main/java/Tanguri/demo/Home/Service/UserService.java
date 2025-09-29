package Tanguri.demo.Home.Service;


import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Domain.UserStatus;
import Tanguri.demo.Home.Dto.ProfileUpdateDto;
import Tanguri.demo.Home.Dto.RankingDto;
import Tanguri.demo.Home.Dto.SearchCond;
import Tanguri.demo.Home.Dto.UserSearchDto;
import Tanguri.demo.Home.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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
        User user = getUserById(userId); // 위에서 만든 메서드 재사용

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

    //인증된 상태인 모든 사용자를 조회
    @Transactional(readOnly = true)
    public List<User> getVerifiedUsers(){
        return userRepository.findByStatus(UserStatus.VERIFIED);
    }

    // 회원가입 인증 거절
    @Transactional
    public void rejectUser(Long userId){
        User user = getUserById(userId);

        //이미 인증된 사용자이니지 아닌지
        if (user.getStatus() == UserStatus.VERIFIED) {
            throw new IllegalStateException("이미 인증된 사용자는 거절할 수 없습니다.");
        }

        //거절된 사용자는 상태를 REJECTED로 변경
        user.rejectUser();
    }

    //랭킹 정보 조회
    @Transactional(readOnly = true)
    public List<RankingDto> getRanking(SearchCond searchCond){
        List<User> users = userRepository.searchUsers(searchCond);

        return users.stream()
                .map(RankingDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserSearchDto> searchUsersByCriteria(String nickname, String club) {
        return userRepository.searchByCriteria(nickname,club).stream()
                .map(UserSearchDto::new)
                .collect(Collectors.toList());
    }
}
