package Tanguri.demo.Home.Dto;

import Tanguri.demo.Home.Domain.User;
import lombok.Getter;

@Getter
public class AuthResponseDto {
    private String token;
    private User user;

    public AuthResponseDto(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
