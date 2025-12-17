package kr.hhplus.be.server.domain.reservation.application;

import kr.hhplus.be.server.domain.reservation.model.Reservation;

public class ReservationMapper {

    public static Reservation toModel(CreateReservationCommand command) {
        return Reservation.create(command.getUserId(), command.getSeatId(), command.getPrice());
    }
}
