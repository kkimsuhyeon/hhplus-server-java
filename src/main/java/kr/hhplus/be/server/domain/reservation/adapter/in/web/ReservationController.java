package kr.hhplus.be.server.domain.reservation.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.application.usecase.ReservationUseCase;
import kr.hhplus.be.server.domain.reservation.adapter.in.web.factory.ReservationCommandFactory;
import kr.hhplus.be.server.domain.reservation.adapter.in.web.request.ReserveSeatRequest;
import kr.hhplus.be.server.shared.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "예약 관리[reservation-controller]", description = "예약 관리 API")
@RestController
@RequestMapping("/api/v1/reservations")
@Validated
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationCommandFactory commandFactory;

    private final ReservationUseCase reservationUseCase;

    @PostMapping
    @Operation(summary = "좌석 예약", description = "좌석 예약 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "정상 예약",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")}
            )
    })
    public ResponseEntity<BaseResponse<Void>> reserveSeat(
            @RequestBody @Valid ReserveSeatRequest request
    ) {
        ReserveSeatCommand command = commandFactory.toReserveSeatCommand(request);
        reservationUseCase.reserve(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success());
    }
}
