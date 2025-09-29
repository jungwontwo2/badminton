package Tanguri.demo.Home.Dto;

import Tanguri.demo.Home.Domain.User;
import lombok.Getter;

@Getter
public class UserSearchDto {
    private Long id;
    private String nickname;
    private String club;
    private String gradeGu;

    public UserSearchDto(User user){
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.club = user.getClub();
        this.gradeGu = user.getGradeGu();
    }
}
