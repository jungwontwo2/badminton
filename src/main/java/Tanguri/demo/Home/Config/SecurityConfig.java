package Tanguri.demo.Home.Config;

import Tanguri.demo.Home.Filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (JWT 사용 시 일반적으로 비활성화)
                .csrf(csrf -> csrf.disable())
                // Spring Security의 CORS 설정을 사용하도록 변경
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션을 사용하지 않음 (Stateless 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //예외 처리 시, 우리가 만든 CustomAuthenticationEntryPoint를 사용하도록 설정
                .exceptionHandling(ex->ex.authenticationEntryPoint(authenticationEntryPoint))
                // HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/test/**").permitAll() // '/auth/**' 경로는 누구나 접근 가능
                        .requestMatchers("/auth/**").permitAll() // '/auth/**' 경로는 누구나 접근 가능
                        .requestMatchers("/api/rankings").permitAll()//랭킹쪽은 누구자 접근 가능
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/matches/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/mypage/**").hasAnyRole("USER","ADMIN")
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                // 직접 만든 JWT 필터를 Spring Security 필터 체인에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        //허용할 출처(프론트 주소) 설정
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        //허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        //허용할 HTTP 헤더
        configuration.setAllowedHeaders(Arrays.asList("*"));
        //쿠키/인증 정보를 함께 보낼 수 있도록 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //모든 경로에 대해 위에서 만든 CORS 설정 적용.
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
}