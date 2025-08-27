package kr.hhplus.be.server.domain.token.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.token.adapter.in.web.response.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Token 관리[token-controller]", description = "Token 관리 API")
@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

    @PostMapping
    @Operation(summary = "Token 생성", description = "Token 생성 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "토큰 정상 생성",
                    content = {@Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TokenResponse.class))}
            )
    })
    public ResponseEntity<TokenResponse> createToken() {
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse());
    }

    @GetMapping
    @Operation(summary = "Token 정보 조회", description = "Token 정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 정보 정상 조회",
                    content = {@Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TokenResponse.class))}
            )
    })
    public ResponseEntity<TokenResponse> getTokenInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse());
    }


}
