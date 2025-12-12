package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.concert.application.repository.SeatRepository;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import kr.hhplus.be.server.domain.concert.model.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository repository;

    @Transactional(readOnly = true)
    public Seat getSeat(String seatId) {
        return repository.findById(seatId)
                .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Seat> getSeatsByScheduleId(String scheduleId) {
        return repository.findByScheduleId(scheduleId);
    }

    @Transactional
    public Seat reserve(String seatId, String userId) {
        Seat seat = repository.findByIdForUpdate(seatId)
                .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));

        seat.reserve(userId);
        return repository.update(seat);
    }

    public Seat confirm(String seatId, String userId) {
        Seat seat = repository.findByIdForUpdate(seatId)
                .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));

        if (!seat.isOwnerBy(userId)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN_ERROR);
        }

        seat.confirm();
        return repository.update(seat);
    }

    @Transactional
    public Seat save(Seat seat) {
        return repository.save(seat);
    }

    @Transactional
    public Seat update(Seat seat) {
        return repository.update(seat);
    }

}
