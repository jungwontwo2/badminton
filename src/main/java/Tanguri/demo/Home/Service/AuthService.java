package Tanguri.demo.Home.Service;

import Tanguri.demo.Home.Domain.Role;
import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Domain.UserStatus;
import Tanguri.demo.Home.Repository.UserRepository;
import Tanguri.demo.Home.Util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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
    private final JwtUtil jwtUtil;

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

    //로그인/가입 시 두 종류의 토큰 모두 생성, Refresh Token을 DB에 저장
    @Transactional
    public User loginOrRegister(User kakaoUser){
        User user = userRepository.findByKakaoId(kakaoUser.getKakaoId())
                .map(existingUser -> {
                    existingUser.updateProfile(kakaoUser.getNickname(), kakaoUser.getProfileImageUrl());
                    return existingUser;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .kakaoId(kakaoUser.getKakaoId())
                            .nickname(kakaoUser.getNickname())
                            .profileImageUrl(kakaoUser.getProfileImageUrl())
                            .role(Role.USER)
                            .status(UserStatus.UNVERIFIED)
                            .build();
                    return userRepository.save(newUser);
                });

        //Refresh Token 생성하고 DB에 저장
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        user.updateRefreshToken(refreshToken);

        return user;
    }

    //Refresh Token으로 새로운 Access Token 발급
    @Transactional
    public String refreshAccessToken(String refreshToken){
        //받은 Refresh Token이 유효한지, 타입이 'refresh'가 맞는지 확인
        if(!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)){
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        //토큰에서 사용자 ID 추출
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        //DB에 저장된 Refresh Token과 사용자가 보낸 토큰이 일치하는지 확인
        if(!user.getRefreshToken().equals(refreshToken)){
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        // 모든 검증 통과하면 새로운 Access Token을 발급하여 반환.
        return jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
    }
}
