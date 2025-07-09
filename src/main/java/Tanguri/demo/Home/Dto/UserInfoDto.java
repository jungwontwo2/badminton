package Tanguri.demo.Home.Dto;


import Tanguri.demo.Home.Domain.User;
import lombok.Getter;

@Getter
public class UserInfoDto {
    private final Long userId;
    private final String nickname;
    private final String profileImageUrl;
    private final String club;
    private final String ageGroup;
    private final String gradeGu;
    private final String gradeSi;
    private final String gradeNational;
    private final Integer mmr;

    public UserInfoDto(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.club = user.getClub();
        this.ageGroup = user.getAgeGroup();
        this.gradeGu = user.getGradeGu();
        this.gradeSi = user.getGradeSi();
        this.gradeNational = user.getGradeNational();
        this.mmr = user.getMmr();
    }
}
