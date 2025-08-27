package kr.hhplus.be.server.domain.concert.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.concert.adapter.in.web.response.ScheduleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "콘서트 관리[concert-controller]", description = "콘서트 관리 API")
@RestController
@RequestMapping("/api/v1/concerts")
public class ConcertController {

    @GetMapping("/{concertId}/schedules")
    @Operation(summary = "콘서트 예약 가능 날짜 조회", description = "콘서트 예약 가능 날짜 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "콘서트 예약 가능 날짜 정상 조회",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ScheduleResponse.class))
                            )
                    }
            )
    })
    public ResponseEntity<List<ScheduleResponse>> getConcertAvailableDate(
            @Parameter(description = "콘서트 ID", in = ParameterIn.PATH)
            @PathVariable(name = "concertId", required = true) Long concertId
    ) {
        return ResponseEntity.ok().body(List.of());
    }
}
