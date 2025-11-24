package kr.hhplus.be.server.domain.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.user.adapter.in.web.mapper.UserCommandMapper;
import kr.hhplus.be.server.domain.user.adapter.in.web.mapper.UserQueryMapper;
import kr.hhplus.be.server.domain.user.adapter.in.web.request.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.adapter.in.web.request.CreateUserRequest;
import kr.hhplus.be.server.domain.user.adapter.in.web.request.FindUserRequest;
import kr.hhplus.be.server.domain.user.adapter.in.web.response.BalanceResponse;
import kr.hhplus.be.server.domain.user.adapter.in.web.response.UserResponse;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.application.dto.query.FindUserQuery;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.model.service.UserService;
import kr.hhplus.be.server.shared.dto.BaseResponse;
import kr.hhplus.be.server.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "유저 관리[user-controller]", description = "유저 관리 API")
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserQueryMapper queryFactory;

    private final UserService userService;
    private final UserCommandMapper userCommandMapper;

    @PatchMapping("/{userId}/balance")
    @Operation(summary = "잔액 충전", description = "잔액 충전 API")
    public ResponseEntity<BaseResponse<Void>> charge(
            @PathVariable(value = "userId") String userId,
            @RequestBody @Valid BalanceChargeRequest request) {

        userService.addBalance(userId, request.getAmount());
        return ResponseEntity.ok().body(BaseResponse.success());
    }

    @GetMapping("/{userId}/balance")
    @Operation(summary = "잔액 조회", description = "잔액 조회 API")
    public ResponseEntity<BaseResponse<BalanceResponse>> getBalanceInfo(
            @PathVariable(value = "userId") String userId
    ) {
        UserEntity user = userService.getUser(userId);

        BalanceResponse response = BalanceResponse.builder()
                .amount(user.getBalance())
                .build();

        return ResponseEntity.ok().body(BaseResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "[DEV] 유저 조회", description = "[DEV] 유저 조회 API")
    public ResponseEntity<BaseResponse<PageResponse<UserResponse>>> getUsers(
            @ParameterObject @ModelAttribute FindUserRequest request,
            @ParameterObject Pageable pageable
    ) {
        FindUserQuery query = queryFactory.toFindQuery(request);
        Page<UserEntity> users = userService.getUsers(query, pageable);
        Page<UserResponse> response = users.map(UserResponse::fromEntity);

        PageResponse<UserResponse> parsedResponse = PageResponse.of(response);
        return ResponseEntity.ok().body(BaseResponse.success(parsedResponse));
    }

    @PostMapping
    @Operation(summary = "[DEV] 유저 생성", description = "[DEV] 유저 생성 API")
    public ResponseEntity<BaseResponse<Void>> createUser(
            @Parameter @RequestBody CreateUserRequest request
    ) {
        CreateUserCommand command = userCommandMapper.toCreateCommand(request);
        userService.create(command);
        return ResponseEntity.ok().body(BaseResponse.success());
    }
}
