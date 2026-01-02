package kr.hhplus.be.server.domain.reservation.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(name = "ReserveSeatRequest", description = "좌석 예약 요청 Request")
@Data
public class ReserveSeatRequest {

    @Schema(description = "좌석 ID")
    @NotNull
    private String seatId;
}
