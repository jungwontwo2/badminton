package Tanguri.demo.Home.Dto;

import Tanguri.demo.Home.Domain.MatchResult;
import Tanguri.demo.Home.Domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AwaitingMatchDto {

    private final Long matchId;
    private final String registeredByName;
    private final String winnerTeam;
    private final String loserTeam;
    private final String score;
    private final LocalDateTime matchDate;

    public AwaitingMatchDto(MatchResult match) {
        this.matchId = match.getId();
        this.registeredByName = match.getRegisteredBy().getNickname();
        this.winnerTeam = formatTeam(match.getWinner1(), match.getWinner2());
        this.loserTeam = formatTeam(match.getLoser1(), match.getLoser2());
        this.score = match.getWinnerScore() + " : " + match.getLoserScore();
        this.matchDate = match.getMatchDate();
    }

    private String formatTeam(User player1,  User player2) {
        if(player2==null){
            return player1.getNickname();
        }
        return player1.getNickname() + " / " + player2.getNickname();
    }


}
