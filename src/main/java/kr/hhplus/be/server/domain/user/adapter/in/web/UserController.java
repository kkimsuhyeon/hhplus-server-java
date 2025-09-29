package kr.hhplus.be.server.domain.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.user.adapter.in.web.request.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.adapter.in.web.response.BalanceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 관리[user-controller]", description = "유저 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    @PatchMapping("/{userId}/balance")
    @Operation(summary = "잔액 충전", description = "잔액 충전 API")
    public ResponseEntity<Void> charge(
            @PathVariable(value = "userId") String userId,
            @RequestBody @Valid BalanceChargeRequest request) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/balance")
    @Operation(summary = "잔액 조회", description = "잔액 조회 API")
    public ResponseEntity<BalanceResponse> getBalanceInfo(
            @PathVariable(value = "userId") String userId
    ) {
        return ResponseEntity.ok().body(new BalanceResponse());
    }
}
