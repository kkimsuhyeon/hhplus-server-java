package kr.hhplus.be.server.domain.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.user.adapter.in.web.request.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.adapter.in.web.response.BalanceResponse;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "유저 관리[user-controller]", description = "유저 관리 API")
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}/balance")
    @Operation(summary = "잔액 충전", description = "잔액 충전 API")
    public ResponseEntity<Void> charge(
            @PathVariable(value = "userId") String userId,
            @RequestBody @Valid BalanceChargeRequest request) {

        userService.addBalance(userId, request.getAmount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/balance")
    @Operation(summary = "잔액 조회", description = "잔액 조회 API")
    public ResponseEntity<BalanceResponse> getBalanceInfo(
            @PathVariable(value = "userId") String userId
    ) {
        UserEntity user = userService.getUser(userId);

        BalanceResponse response = BalanceResponse.builder()
                .amount(user.getBalance())
                .build();

        return ResponseEntity.ok().body(response);
    }
}
