package Tanguri.demo.Home.Dto;

import Tanguri.demo.Home.Domain.User;
import lombok.Getter;

@Getter
public class AuthResponseDto {

    private String accessToken;
    private String refreshToken;
    private User user;

    public AuthResponseDto(String accessToken, String refreshToken, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}
