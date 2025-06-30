package Tanguri.demo.Home.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchRequestDto {

    private Long winner1Id;
    private Long winner2Id; //단식인 경우 null일 수 있음

    private Long loser1Id;
    private Long loser2Id;

    private int winnerScore;
    private int loserScore;
}
