package kr.hhplus.be.server.domain.reservation.adapter.in.scheduler;

import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.reservation.application.ReservationService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationService reservationService;
    private final SeatService seatService;

    @Scheduled(fixedDelay = 60000)
    public void releaseExpiredReservations() {
        List<Reservation> expiredReservations = reservationService.findExpiredPendingReservations();

        expiredReservations.forEach(reservation -> {
            try {
                reservation.cancel();
                reservationService.update(reservation);
                log.info("만료된 예약 취소: reservationId={}, seatId={}", reservation.getId(), reservation.getSeatId());
            } catch (Exception e) {
                log.error("만료 예약 취소 실패: reservationId={}", reservation.getId(), e);
            }
        });

        seatService.releaseExpiredHolds();
    }
}
