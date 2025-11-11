package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatStatus;
import kr.hhplus.be.server.domain.concert.model.exception.SeatErrorCode;
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

        if (!seatEntity.isReservable()) throw new BusinessException(SeatErrorCode.ALREADY_RESERVED);

        UserEntity userEntity = userService.getUser(request.getUserId());

        ReservationEntity reservationEntity = ReservationEntity.create(userEntity, seatEntity);
        reservationRepository.save(reservationEntity);

        seatEntity.setStatus(SeatStatus.RESERVING);
        seatEntity.addReservation(reservationEntity);
    }
}
