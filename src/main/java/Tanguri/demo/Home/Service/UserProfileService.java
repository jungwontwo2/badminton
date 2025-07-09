package Tanguri.demo.Home.Service;

import Tanguri.demo.Home.Domain.MatchResult;
import Tanguri.demo.Home.Domain.MmrChangeLog;
import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.MatchHistoryDto;
import Tanguri.demo.Home.Dto.MmrPointDto;
import Tanguri.demo.Home.Dto.UserInfoDto;
import Tanguri.demo.Home.Dto.UserProfileDto;
import Tanguri.demo.Home.Repository.MatchResultRepository;
import Tanguri.demo.Home.Repository.MmrChangeLogRepository;
import Tanguri.demo.Home.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final MatchResultRepository matchResultRepository;
    private final MmrChangeLogRepository mmrChangeLogRepository;

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfileData(Long userId){
        //프로필 조회할 User 엔티티를 ID로 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        //찾은 User 엔티티 UserInfoDto로 변환
        UserInfoDto userInfoDto = new UserInfoDto(user);

        //해당 유저가 참여한 모든 확정된 경기 기록 조회
        List<MatchResult> matchResults = matchResultRepository.findConfirmedMatchesByUser(user);

        //해당 유저의 모든 MMR 변동 기록을 시간순으로 조회
        List<MmrChangeLog> mmrChangeLogs = mmrChangeLogRepository.findByUserOrderByLogDateAsc(user);

        //조회한 엔티티 리스트들을 각각의 DTO로 변환
        List<MatchHistoryDto> matchHistoryDtos = matchResults.stream()
                .map(match -> new MatchHistoryDto(match, user))
                .collect(Collectors.toList());

        List<MmrPointDto> mmrPointDtos = mmrChangeLogs.stream()
                .map(MmrPointDto::new)
                .collect(Collectors.toList());

        //모든 데이터를 UserProfileDto에 담아 반환
        return new UserProfileDto(userInfoDto,matchHistoryDtos,mmrPointDtos);

    }
}
