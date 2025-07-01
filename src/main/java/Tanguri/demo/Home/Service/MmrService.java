package Tanguri.demo.Home.Service;

import Tanguri.demo.Home.Domain.MatchResult;
import Tanguri.demo.Home.Domain.MatchStatus;
import Tanguri.demo.Home.Domain.MmrChangeLog;
import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.MatchRequestDto;
import Tanguri.demo.Home.Repository.MatchResultRepository;
import Tanguri.demo.Home.Repository.MmrChangeLogRepository;
import Tanguri.demo.Home.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MmrService {

    private final UserRepository userRepository;
    private final MatchResultRepository matchResultRepository;
    private final MmrChangeLogRepository mmrChangeLogRepository;

    private static final int K_FACTOR = 32;

    //사용자가 경기 결과 제출하면 MMR 업데이트를 바로 안하고 일단 '승인 대기' 상태로 저장
    @Transactional
    public void recordMatch(MatchRequestDto matchRequestDto){
        User winner1 = userRepository.findById(matchRequestDto.getWinner1Id()).orElseThrow(EntityNotFoundException::new);
        User loser1 = userRepository.findById(matchRequestDto.getLoser1Id()).orElseThrow(EntityNotFoundException::new);
        User winner2 = userRepository.findById(matchRequestDto.getWinner2Id()).orElseThrow(EntityNotFoundException::new);
        User loser2 = userRepository.findById(matchRequestDto.getLoser2Id()).orElseThrow(EntityNotFoundException::new);

        MatchResult matchResult = MatchResult.builder()
                .winner1(winner1).winner2(winner2)
                .loser1(loser1).loser2(loser2)
                .winnerScore(matchRequestDto.getWinnerScore())
                .loserScore(matchRequestDto.getLoserScore())
                .build();

        matchResultRepository.save(matchResult);
    }

    //팀 MMR 평균 계산
    private double calculateTeamAvgMmr(User player1, User player2) {
        if(player2 == null){
            return player1.getMmr();
        }
        return (double) (player1.getMmr() + player2.getMmr()) / 2;
    }

    private int calculateMmrChange(double teamA_Mmr, double teamB_Mmr){
        double expectedWinrateA = 1.0 / (1.0 + Math.pow(10, (teamB_Mmr - teamA_Mmr) / 400.0));
        double change = K_FACTOR * (1 - expectedWinrateA);
        return (int) Math.round(change);
    }

    private void updatePlayerMmrAndLog(User player, boolean isWinner, int mmrChange, MatchResult matchResult){
        int changeAmount = isWinner ? mmrChange : -mmrChange;
        int newMmr = player.getMmr() + changeAmount;
        player.updateMmr(newMmr);

        MmrChangeLog log = MmrChangeLog.builder()
                .user(player)
                .matchResult(matchResult)
                .changeAmount(changeAmount)
                .resultMmr(newMmr)
                .build();
        mmrChangeLogRepository.save(log);
    }

    //관리자가 승인대기 상태인 경기 결과를 승인해주는 메서드
    @Transactional
    public void confirmMatchAndProcessMmr(Long matchId){
        //승인할 경기 결과 ID로 조회
        MatchResult matchResult = matchResultRepository.findById(matchId).orElseThrow(() -> new EntityNotFoundException("Match not found with id:" + matchId));

        //이미 처리된 경기인지 확인
        if(matchResult.getStatus()!= MatchStatus.PENDING){
            throw new IllegalStateException("이미 처리된 경기 결과입니다.");
        }

        User winner1 = matchResult.getWinner1();
        User winner2 = matchResult.getWinner2();
        User loser1 = matchResult.getLoser1();
        User loser2 = matchResult.getLoser2();

        //MMR 변동량 계산
        double winnersAvgMmr = calculateTeamAvgMmr(winner1, winner2);
        double losersAvgMmr = calculateTeamAvgMmr(loser1, loser2);
        int mmrChange = calculateMmrChange(winnersAvgMmr, losersAvgMmr);

        //경기 결과 상태를 확정으로 변경하고 MMR 변동량 기록
        matchResult.setMmrChange(mmrChange);
        matchResult.confirmMatch();

        //각 선수의 MMR 실제로 업데이트 후 로그 기록
        List<User> players = Arrays.asList(winner1, winner2, loser1, loser2);
        for (User player : players) {
            if(player != null){
                boolean isWinner = (player.equals(winner1) || player.equals(winner2));
                updatePlayerMmrAndLog(player, isWinner, mmrChange, matchResult);
            }

        }
    }
}