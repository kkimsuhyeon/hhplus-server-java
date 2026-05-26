package kr.hhplus.be.server.domain.reservation.application.assembler;

import kr.hhplus.be.server.domain.reservation.application.CreateReservationCommand;
import kr.hhplus.be.server.domain.reservation.model.Reservation;

public class ReservationAssembler {

    public static Reservation toModel(CreateReservationCommand command) {
        return Reservation.create(command.getUserId(), command.getSeatId(), command.getPrice());
    }
}
