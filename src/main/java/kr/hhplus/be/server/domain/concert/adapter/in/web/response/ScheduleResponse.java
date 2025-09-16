package kr.hhplus.be.server.domain.concert.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "ScheduleResponse", description = "콘서트 스케쥴 정보")
public class ScheduleResponse {

    @Schema(description = "콘서트 ID")
    @JsonProperty("concertId")
    private Long concertId;

    @Schema(description = "스케쥴 ID")
    @JsonProperty("scheduleId")
    private Long scheduleId;

    @Schema(description = "콘서트 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate concertDate;

    @Schema(description = "남은 좌석")
    @JsonProperty("availableSeat")
    private int availableSeat;

    @Schema(description = "총 좌석")
    @JsonProperty("totalSeat")
    private int totalSeat;
}
