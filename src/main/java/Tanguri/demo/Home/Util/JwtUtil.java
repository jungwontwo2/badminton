package Tanguri.demo.Home.Util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    //생성자에서 application.properties에 설정한 유효 시간 값들을 주입
    public JwtUtil(@Value("${jwt.secret.key}") String secretKey,
                   @Value("${jwt.access-token.expiration-time}") long accessTokenExpirationTime,
                    @Value("${jwt.refresh-token.expiration-time}") long refreshTokenExpirationTime) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    // Access Token(1시간) 생성
    public String generateAccessToken(Long userId, String role){
        return generateToken(userId, role, accessTokenExpirationTime,"access");
    }

    // Refresh Token(14일) 생성
    public String generateRefreshToken(Long userId){
        return generateToken(userId,null,refreshTokenExpirationTime,"refresh");
    }

    //토큰이 Refresh Token 타입인지 검증
    public boolean isRefreshToken(String token){
        return "refresh".equals(getClaims(token).get("type", String.class));
    }

    // JWT 생성
    public String generateToken(Long userId, String role,long expirationTime, String type) {
        long now = System.currentTimeMillis();
        //Claims: 토큰에 담을 정보의 조각들
        Claims claims = Jwts.claims().setSubject(userId.toString());
        claims.put("type", type);
        if(role != null){
            claims.put("role", role);
        }
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationTime)) // 만료 시간: 24시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 토큰이 유효하지 않은 경우
            return false;
        }
    }

    // JWT에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    //JWT 토큰에서 ROLE 정보 추출
    public String getRoleFromToken(String token){
        return getClaims(token).get("role",String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }
}
