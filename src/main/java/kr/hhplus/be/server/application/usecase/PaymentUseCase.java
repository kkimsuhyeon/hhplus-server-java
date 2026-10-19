package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.payment.application.service.PaymentCommandService;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.reservation.application.service.ReservationService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.application.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentUseCase {

    private final ReservationService reservationService;
    private final UserCommandService userCommandService;
    private final PaymentCommandService paymentCommandService;
    private final SeatService seatService;

    @Transactional
    public Payment pay(PayCommand command) {
        Reservation reservation = reservationService.getReservationForUpdate(command.getReservationId());

        return null;
    }
}



