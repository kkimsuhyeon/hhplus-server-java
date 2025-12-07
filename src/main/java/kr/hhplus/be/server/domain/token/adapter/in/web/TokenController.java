package kr.hhplus.be.server.domain.token.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.token.adapter.in.web.request.IssueTokenRequest;
import kr.hhplus.be.server.domain.token.adapter.in.web.response.TokenResponse;
import kr.hhplus.be.server.domain.token.application.QueueTokenService;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.shared.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Token 관리[token-controller]", description = "Token 관리 API")
@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
public class TokenController {

    private final QueueTokenService queueTokenService;

    @PostMapping
    @Operation(summary = "Token 생성", description = "Token 생성 API")
    public ResponseEntity<BaseResponse<TokenResponse>> issueToken(@RequestBody IssueTokenRequest request) {
        QueueToken token = queueTokenService.issueToken(request.getUserId());
        int rank = queueTokenService.getRank(token.getId());
        TokenResponse tokenResponse = TokenResponse.of(token, rank);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(tokenResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Token 정보 조회", description = "Token 정보 조회 API")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "토큰 정보 정상 조회", content = {@Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TokenResponse.class))})})
    public ResponseEntity<BaseResponse<TokenResponse>> getTokenInfo(@PathVariable(name = "id") String id) {
        QueueToken token = queueTokenService.getToken(id);
        int rank = queueTokenService.getRank(token.getId());
        TokenResponse tokenResponse = TokenResponse.of(token, rank);
        
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(tokenResponse));
    }


}
