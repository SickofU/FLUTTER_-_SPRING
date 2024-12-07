package com.example.demo.controller;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.JwtTokenDto;
import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.entity.Friendship;
import com.example.demo.entity.Member;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@RequiredArgsConstructor  // 필드주입으로 하면 안되고 이렇게 생성자 주입하면은
public class AuthController {
// 여기서 프라이빗 파이널로 객체생성하면 알아서 생성자에 들어감
    private final MemberRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("회원 가입 요청: username={}, friendName={}", request.getUsername(), request.getFriendName());

        // 사용자 이름 중복 확인
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.info("사용자 이름 중복");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\":\"이미 사용 중인 사용자 이름입니다\"}");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 새 사용자 생성
        Member user = Member.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .build();

        userRepository.save(user);
        log.info("멤버 객체 생성 ");
        // 친구 이름이 제공된 경우 친구 관계 설정
        if (request.getFriendName() != null && !request.getFriendName().isEmpty()) {
            Optional<Member> friendOptional = userRepository.findByUsername(request.getFriendName());
            if (friendOptional.isPresent()) {
                Member friend = friendOptional.get();

                // 친구 관계 설정
                user.setFriend(friend);

                log.info("친구 관계 설정됨: {} <-> {}", user.getUsername(), friend.getUsername());
            } else {
                log.warn("친구 이름에 해당하는 사용자가 존재하지 않습니다: {}", request.getFriendName());
            }
        }

        log.info("성공적으로 등록됨: username={}", user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"사용자 등록 성공\"}");
    }

    @PostMapping("/login")

    public ResponseEntity<?> loginUser(@RequestBody Member loginUser, HttpServletResponse response) {
        log.info("Received HTTP POST request to /login\n");
        log.info("로그인 요청 시작: userEmail={}", loginUser.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword())
            );
            log.info("인증 성공: {}", authentication);

            JwtTokenDto tokens = jwtTokenProvider.generateToken(authentication,loginUser.getEmail());
            log.info("토큰 생성 성공: AccessToken={}, RefreshToken={}", tokens.getAccessToken(), tokens.getRefreshToken());

            response.setHeader("Authorization", "Bearer " + tokens.getAccessToken());
            response.setHeader("Refresh-Token", tokens.getRefreshToken());


            Map<String, String> result = new HashMap<>();
            result.put("grantType", tokens.getGrantType());
            result.put("accessToken", tokens.getAccessToken());
            result.put("refreshToken", tokens.getRefreshToken());
            return ResponseEntity.ok(result);
        } catch (BadCredentialsException e) {
            log.error("잘못된 자격 증명으로 로그인 시도", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            log.error("로그인 과정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }

    }


}
