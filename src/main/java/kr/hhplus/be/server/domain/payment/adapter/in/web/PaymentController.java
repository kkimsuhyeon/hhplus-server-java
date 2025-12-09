package kr.hhplus.be.server.domain.payment.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.domain.payment.adapter.in.web.factory.PaymentCommandFactory;
import kr.hhplus.be.server.domain.payment.adapter.in.web.request.PayRequest;
import kr.hhplus.be.server.shared.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Validated
@Tag(name = "결제 관리[payment-controller]", description = "결제 관리 API")
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentCommandFactory commandFactory;

    private final PaymentUseCase paymentUseCase;

    @PostMapping
    @Operation(summary = "결제", description = "결제 API")
    @ApiResponse(
            responseCode = "200",
            description = "정상 결제",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")}
    )
    public ResponseEntity<BaseResponse<Void>> pay(
            @RequestBody @Valid PayRequest request
    ) {
        PayCommand command = commandFactory.toPayCommand(request);
        paymentUseCase.pay(command);
        return ResponseEntity.ok().body(BaseResponse.success());
    }
}
