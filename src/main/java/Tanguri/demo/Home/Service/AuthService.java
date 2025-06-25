package Tanguri.demo.Home.Service;

import Tanguri.demo.Home.Domain.Role;
import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Domain.UserStatus;
import Tanguri.demo.Home.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.client-secret}")
    private String clientSecret;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public String getAccessToken(String code) throws Exception {
        // HTTP 통신을 위한 RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        // 헤더와 바디를 하나의 요청 객체로 만들기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // 카카오 토큰 발급 API에 POST 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON 응답을 파싱하여 액세스 토큰만 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    public User getUserInfo(String accessToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        Long kakaoId = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("kakao_account").get("profile").get("nickname").asText();
        String profileImageUrl = jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText();

        return User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }

    @Transactional
    public User loginOrRegister(User kakaoUser){
        //카카오 ID로 이미 가입된 사용자인지 확인
        return userRepository.findByKakaoId(kakaoUser.getKakaoId())
                .map(user -> {
                    //이미 가입된 경우, 최신 프로필 정보로 업데이트
                    user.updateProfile(kakaoUser.getNickname(), kakaoUser.getProfileImageUrl());
                    return user;
                })
                .orElseGet(()->{
                    //새로 가입하는 경우, 기본값으로 저장
                    User newUser = User.builder()
                            .kakaoId(kakaoUser.getKakaoId())
                            .nickname(kakaoUser.getNickname())
                            .profileImageUrl(kakaoUser.getProfileImageUrl())
                            .role(Role.USER)
                            .status(UserStatus.UNVERIFIED)
                            .build();
                   return userRepository.save(newUser);
                });

    }

}
