package Tanguri.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 이 클래스가 스프링의 설정 파일임을 나타냅니다.
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // "/**"는 모든 경로에 대한 요청을 의미합니다.
                .allowedOrigins("http://localhost:5173") // 허용할 프론트엔드 주소입니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 HTTP 헤더를 허용합니다.
                .allowCredentials(true) // 쿠키/인증 정보를 함께 보낼 수 있도록 허용합니다.
                .maxAge(3600); // Pre-flight 요청의 결과를 캐시할 시간(초)을 설정합니다.
    }
}