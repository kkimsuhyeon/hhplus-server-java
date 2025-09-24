package kr.hhplus.be.server.domain.concert.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.concert.adapter.in.web.factory.ConcertCommandFactory;
import kr.hhplus.be.server.domain.concert.adapter.in.web.factory.ConcertQueryFactory;
import kr.hhplus.be.server.domain.concert.adapter.in.web.request.CreateConcertRequest;
import kr.hhplus.be.server.domain.concert.adapter.in.web.request.FindConcertRequest;
import kr.hhplus.be.server.domain.concert.adapter.in.web.response.ConcertResponse;
import kr.hhplus.be.server.domain.concert.adapter.in.web.response.ScheduleResponse;
import kr.hhplus.be.server.domain.concert.application.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.application.query.FindConcertQuery;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "콘서트 관리[concert-controller]", description = "콘서트 관리 API")
@RestController
@RequestMapping("/api/v1/concerts")
public class ConcertController {

    private final ConcertService concertService;
    private final ConcertQueryFactory queryFactory;
    private final ConcertCommandFactory commandFactory;

    @GetMapping
    @Operation(summary = "콘서트 조회", description = "콘서트 조회 API")
    public ResponseEntity<Page<ConcertResponse>> getConcerts(
            @Parameter(in = ParameterIn.QUERY) @ModelAttribute FindConcertRequest request,
            @ParameterObject Pageable pageable) {

        FindConcertQuery query = queryFactory.toFindQuery(request);
        Page<ConcertEntity> concerts = concertService.getConcerts(query, pageable);

        Page<ConcertResponse> responses = concerts.map(ConcertResponse::fromEntity);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/{concertId}/schedules")
    @Operation(summary = "콘서트 예약 가능 날짜 조회", description = "콘서트 예약 가능 날짜 조회 API")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "콘서트 예약 가능 날짜 정상 조회", content = {@Content(mediaType = "application/json", array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ScheduleResponse.class)))})})
    public ResponseEntity<List<ScheduleResponse>> getConcertAvailableDate(
            @Parameter(description = "콘서트 ID", in = ParameterIn.PATH)
            @PathVariable(name = "concertId", required = true) Long concertId
    ) {
        return ResponseEntity.ok().body(List.of());
    }

    @PostMapping
    @Operation(summary = "콘서트 생성", description = "콘서트 생성 API [DEV]")
    public ResponseEntity<HttpStatus> createConcert(@RequestBody CreateConcertRequest request) {
        CreateConcertCommand command = commandFactory.toCreateCommand(request);
        concertService.createConcert(command);

        return ResponseEntity.ok().build();
    }
}
