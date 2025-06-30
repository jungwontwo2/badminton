package Tanguri.demo.Home.Service;

import Tanguri.demo.Home.Domain.MatchResult;
import Tanguri.demo.Home.Domain.MmrChangeLog;
import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Repository.MatchResultRepository;
import Tanguri.demo.Home.Repository.MmrChangeLogRepository;
import Tanguri.demo.Home.Repository.UserRepository;
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

    @Transactional
    public void recordMatchAndProcessMmr(User winner1, User winner2, User loser1, User loser2, int winnerScore, int loserScore) {
        double winnersAvgMmr = calculateTeamAvgMmr(winner1, winner2);
        double losersAvgMmr = calculateTeamAvgMmr(loser1, loser2);

        int mmrChange = calculateMmrChange(winnersAvgMmr, losersAvgMmr);

        MatchResult matchResult = MatchResult.builder()
                .winner1(winner1).winner2(winner2)
                .loser1(loser1).loser2(loser2)
                .winnerScore(winnerScore).loserScore(loserScore)
                .mmrChange(mmrChange)
                .build();
        matchResultRepository.save(matchResult);

        List<User> players = Arrays.asList(winner1, winner2, loser1, loser2);
        for (User player : players) {
            if(player != null){
                boolean isWinner = (player == winner1 || player == winner2);
                updatePlayerMmrAndLog(player, isWinner, mmrChange, matchResult);
            }
        }
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
}