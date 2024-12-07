package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



import com.example.demo.dto.JwtTokenDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        // 로그인 및 회원가입 경로 제외
        if (requestURI.equals("/api/v1/login") || requestURI.equals("/api/v1/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            JwtTokenDto jwtTokenDto = jwtTokenProvider.resolveToken(request);

            if (jwtTokenDto != null && jwtTokenDto.getAccessToken() != null) {
                if (jwtTokenProvider.validateToken(jwtTokenDto.getAccessToken())) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(jwtTokenDto.getAccessToken());
                    if(authentication != null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("Authentication object: {}", authentication != null ? authentication.getName() : "null");
                    }
                } else {
                    // 토큰 검증 실패 로깅
                    log.info("Invalid token received");
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 여기문제가 잇음
            log.error("JWT Authentication error", e);
            // 에러 응답 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed");
        }
    }
}
