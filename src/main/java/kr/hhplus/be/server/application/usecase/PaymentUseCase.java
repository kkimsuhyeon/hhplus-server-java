package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;
import kr.hhplus.be.server.domain.reservation.model.service.ReservationService;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentUseCase {

    private final ReservationService reservationService;
    private final UserService userService;

    public void pay(PayCommand command) {
        ReservationEntity reservation = reservationService.getReservation(command.getReservationId());
        UserEntity user = userService.getUser(command.getUserId());

        reservation.validateForPayment(command.getUserId());

        BigInteger price = reservation.getSeat().getPrice();
        user.deductBalance(price);

        reservation.completePayment();
    }

    public void cancel() {
    }
}
