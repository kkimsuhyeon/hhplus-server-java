package kr.hhplus.be.server.application.usecase;

import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.payment.application.PaymentService;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.reservation.application.ReservationService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.application.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentUseCase {

    private final ReservationService reservationService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final SeatService seatService;

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
        } catch (BusinessException e) {
            Payment fail = Payment.createFail(command.getReservationId(), BigDecimal.ZERO, e.getMessage());
            paymentService.create(fail);
            throw e;
        } catch (Exception e) {
            log.error("결제 처리 중 예상치 못한 오류 발생: reservationId={}, userId={}, error={}", command.getReservationId(), command.getUserId(), e.getMessage(), e);

            Payment fail = Payment.createFail(command.getReservationId(), BigDecimal.ZERO, "결제 처리 중 알 수 없는 오류가 발생했습니다.");
            paymentService.create(fail);

            throw new RuntimeException("결제 처리 중 알 수 없는 오류가 발생했습니다.", e); // 시스템 예외는 RuntimeException으로 래핑하여 상위로 던집니다.
        }
    }
}



