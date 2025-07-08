package Tanguri.demo.Home.Dto;

import Tanguri.demo.Home.Domain.User;
import lombok.Getter;

@Getter
public class RankingDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private Integer mmr;
    private String club;
    private String gradeGu;
    private String gradeSi;
    private String gradeNational;
    private String ageGroup;

    public RankingDto(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.mmr = user.getMmr();
        this.club = user.getClub();
        this.gradeGu = user.getGradeGu();
        this.ageGroup = user.getAgeGroup();
        this.gradeSi = user.getGradeSi();
        this.gradeNational = user.getGradeNational();
    }
}
