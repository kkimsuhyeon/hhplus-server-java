package kr.hhplus.be.server.domain.payment.adapter.in.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "PayRequest", description = "결제 요청 Request")
public class PayRequest {

    @Schema(description = "예약 ID")
    @JsonProperty("reservationId")
    @NotNull
    private Long reservationId;

    @Schema(description = "잔액")
    @JsonProperty("amount")
    @NotNull
    private Long amount;
}
