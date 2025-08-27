package kr.hhplus.be.server.domain.payment.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.payment.adapter.in.web.request.PayRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "결제 관리[payment-controller]", description = "결제 관리 API")
@RestController
@RequestMapping("/api/v1/payments")
@Validated
public class PaymentController {

    @PostMapping
    @Operation(summary = "결제", description = "결제 API")
    @ApiResponse(
            responseCode = "200",
            description = "정상 결제",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")}
    )
    public ResponseEntity<Void> pay(
            @RequestBody @Valid PayRequest request
    ) {
        return ResponseEntity.ok().build();
    }
}
