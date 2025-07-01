package Tanguri.demo.Home.Dto;

import Tanguri.demo.Home.Domain.MatchResult;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PendingMatchDto {
    private Long matchId;
    private String winner1Name;
    private String winner2Name;
    private String loser1Name;
    private String loser2Name;
    private Integer winnerScore;
    private Integer loserScore;
    private LocalDateTime matchDate;

    public PendingMatchDto(MatchResult matchResult){
        this.matchId = matchResult.getId();
        this.winner1Name = matchResult.getWinner1().getNickname();
        this.winner2Name = (matchResult.getWinner2() != null) ? matchResult.getWinner2().getNickname() : "";
        this.loser1Name = matchResult.getLoser1().getNickname();
        this.loser2Name = (matchResult.getLoser2() != null) ? matchResult.getLoser2().getNickname() : "";
        this.winnerScore = matchResult.getWinnerScore();
        this.loserScore = matchResult.getLoserScore();
        this.matchDate = matchResult.getMatchDate();
    }
}
