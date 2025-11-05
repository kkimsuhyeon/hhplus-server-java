package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatStatus;
import kr.hhplus.be.server.domain.concert.model.service.ConcertService;
import kr.hhplus.be.server.domain.concert.model.service.SeatService;
import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;
import kr.hhplus.be.server.domain.reservation.model.repository.ReservationRepository;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationUseCase {

    private final SeatService seatService;
    private final UserService userService;

    private final ReservationRepository reservationRepository;

    public void execute(ReserveSeatCommand request) {
        SeatEntity seatEntity = seatService.getSeat(request.getSeatId());

        if (!seatEntity.isReservable()) throw new IllegalArgumentException("예약이 불가능한 좌석");

        UserEntity userEntity = userService.getUser(request.getUserId());

        ReservationEntity reservationEntity = ReservationEntity.create(userEntity, seatEntity);
        reservationRepository.save(reservationEntity);

        seatEntity.setStatus(SeatStatus.RESERVING);
        seatEntity.addReservation(reservationEntity);
    }
}
