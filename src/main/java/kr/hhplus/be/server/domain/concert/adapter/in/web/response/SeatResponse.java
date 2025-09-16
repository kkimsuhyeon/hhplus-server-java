package kr.hhplus.be.server.domain.concert.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.concert.model.entity.SeatStatus;

@Schema(name = "SeatResponse", description = "콘서트 좌석 정보")
public class SeatResponse {

    @Schema(description = "콘서트 ID")
    @JsonProperty("concertId")
    private Long concertId;

    @Schema(description = "스케쥴 ID")
    @JsonProperty("scheduleId")
    private Long scheduleId;

    @Schema(description = "좌석 ID")
    @JsonProperty("seatId")
    private Long seatId;

    @Schema(description = "좌석 상태")
    @JsonProperty("status")
    private SeatStatus status;
}
