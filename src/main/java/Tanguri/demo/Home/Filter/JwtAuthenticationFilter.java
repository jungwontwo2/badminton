package Tanguri.demo.Home.Filter;

import Tanguri.demo.Home.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        Long userId = null;

        // "Bearer "로 시작하는 토큰이 있는지 확인
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    userId = jwtUtil.getUserIdFromToken(token);
                }
            } catch (Exception e) {
                // ⭐ [수정] : logger.error 대신 간단한 콘솔 출력으로 변경하여 에러를 방지합니다.
                System.out.println("Invalid JWT Token: " + e.getMessage());
            }
        }

        // 토큰이 유효하고, 현재 SecurityContext에 인증 정보가 없는 경우
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Spring Security가 이해할 수 있는 UserDetails 객체 생성 (실제 DB 조회는 생략)
            UserDetails userDetails = new User(userId.toString(), "", new ArrayList<>());

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}