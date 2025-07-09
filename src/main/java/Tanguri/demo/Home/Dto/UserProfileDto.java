package Tanguri.demo.Home.Dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UserProfileDto {

    private final UserInfoDto userInfo;
    private final List<MatchHistoryDto> matchHistories;
    private final List<MmrPointDto> mmrPoints;

    public UserProfileDto(UserInfoDto userInfo, List<MatchHistoryDto> matchHistories, List<MmrPointDto> mmrPoints) {
        this.userInfo = userInfo;
        this.matchHistories = matchHistories;
        this.mmrPoints = mmrPoints;
    }
}
