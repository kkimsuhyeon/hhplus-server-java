package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.PayCommand;
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
public class PaymentUseCase {

    private final ReservationService reservationService;
    private final UserService userService;

    public void pay(PayCommand command) {
        Reservation reservation = reservationService.getReservation(command.getReservationId());
        reservation.validateForPayment(command.getUserId());

        userService.deductBalance(command.getUserId(), reservation.getPaymentAmount());
        reservation.completePayment();

        reservationService.update(reservation);
    }

    public void cancel() {
    }
}
