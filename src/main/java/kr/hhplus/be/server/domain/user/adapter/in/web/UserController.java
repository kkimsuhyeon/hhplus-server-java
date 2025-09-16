package kr.hhplus.be.server.domain.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.user.adapter.in.web.request.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.adapter.in.web.response.BalanceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 관리[user-controller]", description = "유저 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    @PostMapping("/balance/charge")
    @Operation(summary = "잔액 충전", description = "잔액 충전 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "잔액 정상 충전",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")}
            )
    })
    public ResponseEntity<Void> charge(
            @RequestBody @Valid BalanceChargeRequest request
    ) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance")
    @Operation(summary = "잔액 조회", description = "잔액 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "잔액 정상 조회",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BalanceResponse.class)
                            )
                    }

            )
    })
    public ResponseEntity<BalanceResponse> getBalanceInfo() {
        return ResponseEntity.ok().body(new BalanceResponse());
    }
}
