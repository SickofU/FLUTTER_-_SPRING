package com.example.demo.config;

import static java.lang.System.getenv;


import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.demo.dto.JwtTokenDto;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.UserDetailServiceImp;
import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import io.github.cdimascio.dotenv.Dotenv;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserDetailServiceImp userDetailServiceImp;

    public static final Dotenv dotenv = Dotenv.load();
    private String secretKey = Base64.getEncoder().encodeToString(
            Objects.requireNonNull(dotenv.get("JWT_SECRET")).getBytes());
    private final MemberRepository memberRepository;
    private static final String AUTHORITIES_KEY = "ROLE_USER";

    public JwtTokenDto generateToken(Authentication authentication,String email) {

        long currentTime = (new Date()).getTime();

        Date accessTokenExpirationTime = new Date(currentTime + (1000 * 60 * 60 * 24 * 7));
        Date refreshTokenExpirationTime = new Date(currentTime + (1000 * 60 * 60 * 24 * 7));

        Claims claims = Jwts.claims().setSubject(email);
        claims.put(AUTHORITIES_KEY, authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        claims.put("email", email);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(accessTokenExpirationTime)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(refreshTokenExpirationTime)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Error("유저를 찾을 수 없습니다."));
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);
        log.info("Make new Token");

        return JwtTokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }



    public Authentication getAuthentication(String accessToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(accessToken)
                .getBody();

        log.info("Token Claims: {}", claims);
        String email = claims.getSubject();

        // Member 객체 가져오기
        Member member = (Member) userDetailServiceImp.loadUserByUsername(email);
        if(member == null){
            log.error("Failed to load Member for email: {}",email);
            return null;
        }
        log.info("Loaded Member: {}", member.getEmail());
        // 여기 리턴값 널로 바꿈 원래 액세스토큰인데
        return new UsernamePasswordAuthenticationToken(member, accessToken, member.getAuthorities());
    }

    //액세스 토큰과 리프레시 토큰 함께 재발행
    public JwtTokenDto reissueToken(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Error("유저를 찾을 수 없습니다."));

        Authentication authentication = getAuthentication(email);

        return generateToken(authentication, member.getEmail());
    }

    public JwtTokenDto resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Refresh-Token");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7); // "Bearer " 제거
            log.info("Authorization Header: {}", request.getHeader("Authorization"));
            log.info("Refresh-Token Header: {}", request.getHeader("Refresh-Token"));

            return JwtTokenDto.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        log.info("Authorization Header: {}", request.getHeader("Authorization"));
        log.info("Refresh-Token Header: {}", request.getHeader("Refresh-Token"));

        return null;
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {

            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {

            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {

            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {

            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public String getEmail(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }
}
