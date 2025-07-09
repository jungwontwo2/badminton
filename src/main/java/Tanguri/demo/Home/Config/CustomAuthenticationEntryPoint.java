package Tanguri.demo.Home.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때,
        //403 Forbidden 대신 401 Unautorized 에러 반환
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");

    }
}
