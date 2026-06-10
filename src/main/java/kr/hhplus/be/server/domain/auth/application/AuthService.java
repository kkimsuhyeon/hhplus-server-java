package kr.hhplus.be.server.domain.auth.application;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.security.jwt.JwtTokenPayload;
import kr.hhplus.be.server.config.security.jwt.JwtTokenProvider;
import kr.hhplus.be.server.domain.auth.adapter.in.web.response.TokenResponse;
import kr.hhplus.be.server.domain.auth.application.dto.command.SignInCommand;
import kr.hhplus.be.server.domain.auth.application.dto.command.SignUpCommand;
import kr.hhplus.be.server.domain.auth.exception.AuthErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.port.UserRepository;
import kr.hhplus.be.server.domain.user.service.UserRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRegistration userRegistration;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse signIn(SignInCommand command) {
        User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.NO_EXISTS_USER));

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new BusinessException(AuthErrorCode.INCORRECT_PASSWORD);
        }

        JwtTokenPayload tokenPayload = JwtTokenPayload.from(user);
        String accessToken = jwtTokenProvider.createAccessToken(tokenPayload);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void signUp(SignUpCommand command) {
        User user = userRegistration.register(command.getEmail(), command.getPassword());
        userRepository.save(user);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String userId = jwtTokenProvider.getUserId(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.NO_EXISTS_USER));

        JwtTokenPayload tokenPayload = JwtTokenPayload.from(user);
        String newAccessToken = jwtTokenProvider.createAccessToken(tokenPayload);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

}
