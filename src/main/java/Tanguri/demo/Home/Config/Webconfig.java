//package Tanguri.demo.Home.Config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class Webconfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry){
//        registry.addMapping("/**") // /api로 시작하는 모든 요청에 대해
//                .allowedOrigins("http://localhost:5173")   //http:localhost:5173(React 앱)의 요청을 허용
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true)//쿠키/인증 정보 포함 허용
//                .maxAge(3600);//pre-flight 요청의 캐시 시간(초)
//    }
//}
