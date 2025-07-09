package Tanguri.demo.Home.Filter;

import Tanguri.demo.Home.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
        String role = null;

        // "Bearer "로 시작하는 토큰이 있는지 확인
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    userId = jwtUtil.getUserIdFromToken(token);
                    role = jwtUtil.getRoleFromToken(token);
                }
            } catch (Exception e) {
                // ⭐ [수정] : logger.error 대신 간단한 콘솔 출력으로 변경하여 에러를 방지합니다.
                System.out.println("Invalid JWT Token: " + e.getMessage());
            }
        }

        // 토큰이 유효하고, 현재 SecurityContext에 인증 정보가 없는 경우
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // ⭐ [핵심 수정] : 역할 정보로 Spring Security용 권한 목록 생성
            // Spring Security는 역할 이름 앞에 "ROLE_" 접두사가 붙어야 권한으로 인식합니다.
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

            // 인증 토큰 생성 시 사용자 ID와 함께 '권한 목록'을 전달합니다.
            // 첫 번째 인자(principal)는 나중에 @AuthenticationPrincipal 로 편하게 꺼내 쓰기 위해 User 객체 대신 userId.toString()을 넣습니다.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userId.toString(), null, authorities);

            // SecurityContext에 인증 정보(사용자 ID + 권한)를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}