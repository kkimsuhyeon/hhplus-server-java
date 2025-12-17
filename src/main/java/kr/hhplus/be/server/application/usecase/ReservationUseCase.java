package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.application.CreateReservationCommand;
import kr.hhplus.be.server.domain.reservation.application.ReservationService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.application.UserService;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationUseCase {

    private final SeatService seatService;
    private final UserService userService;
    private final ReservationService reservationService;

    public Reservation reserve(ReserveSeatCommand command) {
        User user = userService.getUser(command.getUserId());
        Seat seat = seatService.reserve(command.getSeatId(), user.getId());

        CreateReservationCommand createReservationCommand = CreateReservationCommand.builder()
                .userId(user.getId())
                .seatId(seat.getId())
                .price(seat.getPrice())
                .build();

        return reservationService.create(createReservationCommand);
    }
}
