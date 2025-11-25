package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.repository.SeatRepository;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import kr.hhplus.be.server.domain.reservation.application.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 예약 UseCase
 * JPA Entity를 전혀 모르며, 순수 도메인 모델과 Repository 인터페이스만 사용
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationUseCase {

    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public Reservation execute(ReserveSeatCommand request) {
        // 1. 좌석 조회 (비관적 락)
        Seat seat = seatRepository.findByIdForUpdate(request.getSeatId());

        // 2. 예약 가능 여부 검증
        if (!seat.isReservable()) {
            throw new BusinessException(SeatErrorCode.ALREADY_RESERVED);
        }

        // 3. 사용자 조회 (존재 여부 확인)
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. 예약 생성 (도메인 모델)
        Reservation reservation = Reservation.create(
                user.getId(),
                seat.getId(),
                seat.getPrice()
        );

        // 5. 좌석 상태 변경
        seat.reserve();

        // 6. 저장
        seatRepository.save(seat);
        return reservationRepository.save(reservation);
    }
}
