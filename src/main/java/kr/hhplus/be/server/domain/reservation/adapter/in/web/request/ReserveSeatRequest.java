package kr.hhplus.be.server.domain.reservation.adapter.in.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ReserveSeatRequest", description = "좌석 예약 요청 Request")
public class ReserveSeatRequest {

    @Schema(description = "콘서트 ID")
    @JsonProperty("concertId")
    @NotNull
    private Long concertId;

    @Schema(description = "스케쥴 ID")
    @JsonProperty("scheduleId")
    @NotNull
    private Long scheduleId;

    @Schema(description = "좌석 ID")
    @JsonProperty("seatId")
    @NotNull
    private Long seatId;
}
