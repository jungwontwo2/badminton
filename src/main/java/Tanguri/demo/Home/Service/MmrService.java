package Tanguri.demo.Home.Service;

import Tanguri.demo.Home.Domain.MatchResult;
import Tanguri.demo.Home.Domain.MatchStatus;
import Tanguri.demo.Home.Domain.MmrChangeLog;
import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.AwaitingMatchDto;
import Tanguri.demo.Home.Dto.MatchRequestDto;
import Tanguri.demo.Home.Dto.PendingMatchDto;
import Tanguri.demo.Home.Repository.MatchResultRepository;
import Tanguri.demo.Home.Repository.MmrChangeLogRepository;
import Tanguri.demo.Home.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MmrService {

    private final UserRepository userRepository;
    private final MatchResultRepository matchResultRepository;
    private final MmrChangeLogRepository mmrChangeLogRepository;

    private static final int K_FACTOR = 32;

    //경기 결과를 '상대방 확인 대기' 상태로 저장
    @Transactional
    public void recordMatch(MatchRequestDto matchRequestDto, User registeredBy) {
        User winner1 = userRepository.findById(matchRequestDto.getWinner1Id()).orElseThrow(() -> new EntityNotFoundException("Winner1 not found"));
        User loser1 = userRepository.findById(matchRequestDto.getLoser1Id()).orElseThrow(() -> new EntityNotFoundException("Loser1 not found"));

        User winner2 = (matchRequestDto.getWinner2Id() != null) ? userRepository.findById(matchRequestDto.getWinner2Id())
                .orElseThrow(() -> new EntityNotFoundException("Winner2 not found")) : null;
        User loser2 = (matchRequestDto.getLoser2Id() != null) ? userRepository.findById(matchRequestDto.getLoser2Id())
                .orElseThrow(() -> new EntityNotFoundException("Loser2 not found")) : null;

        // ✅ [추가] 중복된 선수가 있는지 서버에서 최종 검증합니다.
        List<User> players = new ArrayList<>();
        players.add(winner1);
        players.add(loser1);
        if (winner2 != null) players.add(winner2);
        if (loser2 != null) players.add(loser2);

        long distinctPlayerCount = players.stream().distinct().count();
        if (players.size() != distinctPlayerCount) {
            throw new IllegalStateException("한 경기에 동일한 선수를 중복해서 등록할 수 없습니다.");
        }


        // 등록자가 경기의 참여자인지 확인하는 보안 검증 로직
        List<User> participants = Arrays.asList(winner1, winner2, loser1, loser2);
        boolean isParticipant = participants.contains(registeredBy);

        if (!isParticipant) {
            throw new IllegalStateException("경기 결과를 등록하는 사용자는 반드시 경기의 참여자여야 합니다.");
        }

        MatchResult matchResult = MatchResult.builder()
                .winner1(winner1).winner2(winner2)
                .loser1(loser1).loser2(loser2)
                .winnerScore(matchRequestDto.getWinnerScore())
                .loserScore(matchRequestDto.getLoserScore())
                .registeredBy(registeredBy)
                .build();

        matchResultRepository.save(matchResult);
    }

    //관리자 페이지에 보여줄 '승인 대기' 상태인 모든 경기 결과 조회
    @Transactional(readOnly = true)
    public List<PendingMatchDto> getPendingMatches(){
        //승인대기 상태인 경기결과 가져오기
        List<MatchResult> pendingMatches = matchResultRepository.findByStatus(MatchStatus.PENDING_ADMIN);

        // 해당 엔티티 리스트를 DTO 리스트로 변환하여 반환
        return pendingMatches.stream()
                .map(PendingMatchDto::new)
                .collect(Collectors.toList());
    }

    //경기 결과 거부
    @Transactional
    public void rejectMatch(Long matchId){
        MatchResult matchResult = matchResultRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match Not Found with id: " + matchId));

        if(matchResult.getStatus() != MatchStatus.PENDING_ADMIN){
            throw new IllegalStateException("관리자 승인 대기 상태의 경기만 처리할 수 있습니다.");
        }
        //MMR 계산 없이 상태만 REJECTED로 변경
        matchResult.rejectMatch();
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

    //현재 로그인한 사용자가 상대팀으로서 확인해야 할 경기 목록 조회
    @Transactional(readOnly = true)
    public List<AwaitingMatchDto> getMatchesAwaitingConfirmation(User currentUser){
        // repository에 추가한 메서드 호출
        List<MatchResult> awaitingMatches = matchResultRepository.findMatchesAwaitingOpponentConfirmation(currentUser);

        //조회한 엔티티 리스트를 프론트엔드가 사용하기 좋은 DTO 리스트로 변환
        return awaitingMatches.stream()
                .map(AwaitingMatchDto::new) //.map(match -> new AwaitingMatchDto(match))와 동일
                .collect(Collectors.toList());
    }


    //상대방이 경기 결과를 '확인'
    @Transactional
    public void confirmMatchByOpponent(Long matchId, User opponent){
        //확인할 경기 결과를 ID로 조회
        MatchResult matchResult = matchResultRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match notn found with id: " + matchId));

        //'상대방 확인 대기' 상태인지 확인
        if (matchResult.getStatus() != MatchStatus.AWAITING_OPPONENT) {
            throw new IllegalStateException("상대방의 확인을 기다리는 경기가 아닙니다.");
        }

        // 요청을 보낸 사용자가 등록자가 아닌지 확인
        if (matchResult.getRegisteredBy().equals(opponent)) {
            throw new IllegalStateException("결과를 등록한 사용자는 확인할 수 없습니다.");
        }

        //(추가 검사) 요청을 보낸 사용자가 해당 경기의 참여자인지 확인
        boolean isParticipant = matchResult.getWinner1().equals(opponent) ||
                (matchResult.getWinner2() != null && matchResult.getWinner2().equals(opponent)) ||
                matchResult.getLoser1().equals(opponent) ||
                (matchResult.getLoser2() != null && matchResult.getLoser2().equals(opponent));

        if(!isParticipant){
            throw new IllegalStateException("해당 경기의 참여자만 결과를 확인할 수 있습니다.");
        }

        //모든 검사 통과 시 상태를 '관리자 승인 대기'로 변경
        matchResult.confirmByOpponent();
    }

    //관리자가 승인대기 상태인 경기 결과를 승인해주는 메서드
    @Transactional
    public void confirmMatchByAdminAndProcessMmr(Long matchId){
        //승인할 경기 결과 ID로 조회
        MatchResult matchResult = matchResultRepository.findById(matchId).orElseThrow(() -> new EntityNotFoundException("Match not found with id:" + matchId));

        //이미 처리된 경기인지 확인
        if(matchResult.getStatus()!= MatchStatus.PENDING_ADMIN){
            throw new IllegalStateException("관리자 승인 대기 상태의 경기만 처리할 수 있습니다.");
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
        matchResult.confirmByAdmin();//관리자가 상태를 CONFIRMED로 변경

        //각 선수의 MMR 실제로 업데이트 후 로그 기록
        List<User> players = Arrays.asList(winner1, winner2, loser1, loser2);
        for (User player : players) {
            if(player != null){
                boolean isWinner = (player.equals(winner1) || player.equals(winner2));
                updatePlayerMmrAndLog(player, isWinner, mmrChange, matchResult);
            }

        }
    }

    //현재 로그인한 사용자가 상대팀으로서 확인해야 할 경기 목록 조회
    @Transactional(readOnly = true)
    public List<AwaitingMatchDto> getMatchesAwaitingMyConfirmation(User currentUser) {
        List<MatchResult> awaitingMatches = matchResultRepository.findMatchesAwaitingOpponentConfirmation(currentUser);
        return awaitingMatches.stream()
                .map(AwaitingMatchDto::new)
                .collect(Collectors.toList());
    }


    // 현재 로그인한 사용자가 등록했고 상대방의 확인을 기다리는 경기 목록을 조회.
    @Transactional(readOnly = true)
    public List<AwaitingMatchDto> getMatchesIRegisteredAndAwaiting(User currentUser){
        List<MatchResult> awaitingMatches = matchResultRepository.findByRegisteredByAndStatus(currentUser, MatchStatus.AWAITING_OPPONENT);
        return awaitingMatches.stream()
                .map(AwaitingMatchDto::new)
                .collect(Collectors.toList());
    }
}