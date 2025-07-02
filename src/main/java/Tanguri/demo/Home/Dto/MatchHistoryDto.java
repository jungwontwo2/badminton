package Tanguri.demo.Home.Dto;

import Tanguri.demo.Home.Domain.MatchResult;
import Tanguri.demo.Home.Domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MatchHistoryDto {

    private final Long matchId;
    private final String result;
    private final String myPartner;
    private final String opponents;
    private final String score;
    private final Integer mmrChange;
    private final LocalDateTime matchDate;

    //MatchResult와 현재 사용자를 받아서 DTO를 생성하는 생성자
    public MatchHistoryDto(MatchResult match, User currentUser) {
        this.matchId = match.getId();
        this.matchDate = match.getMatchDate();
        this.score = match.getWinnerScore() + " : " + match.getLoserScore();

        //객체 비교 대신 고유 ID로 비교하고, null 가능성을 더 안전하게 처리
        Long currentUserId = currentUser.getId();
        User winner1 = match.getWinner1();
        User winner2 = match.getWinner2();
        User loser1 = match.getLoser1();
        User loser2 = match.getLoser2();

        boolean isWinner = winner1.getId().equals(currentUserId) || (winner2 != null && winner2.getId().equals(currentUserId));

        if (isWinner) {
            this.result = "승";
            this.mmrChange = match.getMmrChange();
            this.myPartner = getPartnerName(winner1, winner2, currentUser);
            this.opponents = getOpponentNames(loser1, loser2);
        } else {
            this.result = "패";
            this.mmrChange = -match.getMmrChange();
            this.myPartner = getPartnerName(loser1, loser2, currentUser);
            this.opponents = getOpponentNames(winner1, winner2);
        }
    }

    private String getPartnerName(User player1, User player2, User currentUser){
        if(player2 == null)//파트너 없으면 X (단식)
            return "X (단식)";
        //객체 비교 대신 ID로 비교
        return player1.getId().equals(currentUser.getId()) ? player2.getNickname() : player1.getNickname();    }

    private String getOpponentNames(User player1, User player2){
        if(player2 == null){
            return player1.getNickname();//단식이면 상대 한사람 닉네임만 반환
        }
        return player1.getNickname() + " / " + player2.getNickname();
    }

}
