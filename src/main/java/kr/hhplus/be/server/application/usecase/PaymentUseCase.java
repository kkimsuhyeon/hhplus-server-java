package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.domain.reservation.application.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결제 UseCase
 * 순수 도메인 Model만 다루며, JPA Entity에 대해 전혀 알지 못함
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentUseCase {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public void pay(PayCommand command) {
        // 1. 순수 도메인 Model을 조회
        Reservation reservation = reservationRepository.findById(command.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 도메인 Model의 비즈니스 메서드 호출
        reservation.validateForPayment(command.getUserId());
        user.deductBalance(new java.math.BigDecimal(reservation.getSeatPrice()));
        reservation.completePayment();

        // 3. 변경된 Model을 저장
        reservationRepository.save(reservation);
        userRepository.save(user);
    }

    public void cancel() {
    }
}
