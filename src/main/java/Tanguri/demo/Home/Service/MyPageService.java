package Tanguri.demo.Home.Service;

import Tanguri.demo.Home.Domain.MatchResult;
import Tanguri.demo.Home.Domain.MmrChangeLog;
import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.MatchHistoryDto;
import Tanguri.demo.Home.Dto.MmrPointDto;
import Tanguri.demo.Home.Dto.MyPageDto;
import Tanguri.demo.Home.Repository.MatchResultRepository;
import Tanguri.demo.Home.Repository.MmrChangeLogRepository;
import Tanguri.demo.Home.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final MatchResultRepository matchResultRepository;
    private final MmrChangeLogRepository mmrChangeLogRepository;

    @Transactional(readOnly = true)
    public MyPageDto getMyPageDte(Long userId){
        //1. 기준이 되는 User 엔티티 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        //2. 해당 유저가 참여한 모든 확정된 경기 기록 조회
        List<MatchResult> matchResults = matchResultRepository.findConfirmedMatchesByUser(user);

        //3. 해당 유저의 모든 MMR 변동 기록을 시간순으로 조회
        List<MmrChangeLog> mmrChangeLogs = mmrChangeLogRepository.findByUserOrderByLogDateAsc(user);

        //4. 조회한 엔티티 리스트들 프론트에서 사용하기 좋게 DTO 리스트로 변환
        List<MatchHistoryDto> matchHistoryDtos = matchResults.stream()
                .map(match -> new MatchHistoryDto(match, user))
                .collect(Collectors.toList());

        List<MmrPointDto> mmrPointDtos = mmrChangeLogs.stream()
                .map(MmrPointDto::new)
                .collect(Collectors.toList());

        //5. 2개 DTO 리스트를 최종 DTO에 담아 반환
        return new MyPageDto(matchHistoryDtos, mmrPointDtos);
    }
}
