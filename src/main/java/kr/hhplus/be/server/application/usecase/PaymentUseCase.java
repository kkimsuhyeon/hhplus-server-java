package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.payment.application.PaymentService;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.reservation.application.ReservationService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentUseCase {

    private final ReservationService reservationService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final SeatService seatService;

    public void pay(PayCommand command) {
        Reservation reservation = reservationService.getReservation(command.getReservationId());
        reservation.validateForPayment(command.getUserId());

        userService.deductBalance(command.getUserId(), reservation.getPaymentAmount());

        Payment payment = Payment.createSuccess(reservation.getId(), reservation.getPaymentAmount());
        paymentService.create(payment);

        reservation.completePayment();
        reservationService.update(reservation);

        seatService.confirm(reservation.getSeatId(), command.getUserId());
    }
}
