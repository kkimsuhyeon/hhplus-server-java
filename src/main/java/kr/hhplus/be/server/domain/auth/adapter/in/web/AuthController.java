package kr.hhplus.be.server.domain.auth.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.auth.adapter.in.web.mapper.AuthCommandMapper;
import kr.hhplus.be.server.domain.auth.adapter.in.web.request.SignInRequest;
import kr.hhplus.be.server.domain.auth.adapter.in.web.request.SignUpRequest;
import kr.hhplus.be.server.domain.auth.adapter.in.web.response.TokenResponse;
import kr.hhplus.be.server.domain.auth.application.AuthService;
import kr.hhplus.be.server.domain.auth.application.dto.command.SignInCommand;
import kr.hhplus.be.server.domain.auth.application.dto.command.SignUpCommand;
import kr.hhplus.be.server.shared.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "인증 관리[auth-controller]", description = "인증 관리 API")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/sign-in")
    @Operation(summary = "로그인", description = "로그인 API")
    public ResponseEntity<BaseResponse<TokenResponse>> signIn(@RequestBody SignInRequest request) {
        SignInCommand command = AuthCommandMapper.toSignInCommand(request);
        TokenResponse tokenResponse = authService.signIn(command);

        return ResponseEntity.ok().body(BaseResponse.success(tokenResponse));
    }

    @PostMapping(value = "/sign-up")
    @Operation(summary = "회원가입", description = "회원가입 API")
    public ResponseEntity<BaseResponse<Void>> signUp(@RequestBody SignUpRequest request) {
        SignUpCommand command = AuthCommandMapper.toSignUpCommand(request);
        authService.signUp(command);
        return ResponseEntity.ok().body(BaseResponse.success());
    }

}
