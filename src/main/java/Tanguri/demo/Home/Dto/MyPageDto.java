package Tanguri.demo.Home.Dto;


import lombok.Getter;

import java.util.List;

@Getter
public class MyPageDto {

    private final List<MatchHistoryDto> matchHistories;
    private final List<MmrPointDto> mmrPoints;

    public MyPageDto(List<MatchHistoryDto> matchHistories, List<MmrPointDto> mmrPoints) {
        this.matchHistories = matchHistories;
        this.mmrPoints = mmrPoints;
    }
}
