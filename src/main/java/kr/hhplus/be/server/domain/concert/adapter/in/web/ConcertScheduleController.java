package kr.hhplus.be.server.domain.concert.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.concert.adapter.in.web.response.SeatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "콘서트 스케쥴 관리[concert-schedule-controller]", description = "콘서트 스케쥴 관리 API")
@RestController
@RequestMapping("/api/v1/schedules")
public class ConcertScheduleController {

    @GetMapping("/{scheduleId}/seats")
    @Operation(summary = "콘서트 예약 가능 좌석 조회", description = "콘서트 예약 가능 좌성 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "콘서트 예약 가능 좌석 정상 조회",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SeatResponse.class)
                            )
                    }
            )
    })
    public ResponseEntity<SeatResponse> getConcertAvailableSeat(
            @Parameter(description = "스케쥴 ID", in = ParameterIn.PATH)
            @PathVariable(name = "scheduleId", required = true) Long scheduleId
    ) {
        return ResponseEntity.ok().body(new SeatResponse());
    }

}
