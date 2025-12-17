package kr.hhplus.be.server.application.usecase;

import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.payment.application.PaymentService;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.reservation.application.ReservationService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentUseCase {

    private final ReservationService reservationService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final SeatService seatService;

    @Retryable(retryFor = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2))
    @Transactional
    public Payment pay(PayCommand command) {
        try {
            Reservation reservation = reservationService.getReservationForUpdate(command.getReservationId());
            reservation.validateForPayment(command.getUserId());

            userService.deductBalance(command.getUserId(), reservation.getPaymentAmount());

            reservation.completePayment();
            reservationService.update(reservation);

            seatService.confirm(reservation.getSeatId(), command.getUserId());

            Payment payment = Payment.createSuccess(reservation.getId(), reservation.getPaymentAmount());
            return paymentService.create(payment);
        } catch (Exception e) {
//            Payment.createFail()
            throw e;
        }
    }
}



