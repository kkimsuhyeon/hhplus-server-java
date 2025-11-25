package kr.hhplus.be.server.domain.concert.application;

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
        return repository.findById(seatId);
    }

    @Transactional(readOnly = true)
    public Seat getSeatForUpdate(String seatId) {
        return repository.findByIdForUpdate(seatId);
    }

    @Transactional(readOnly = true)
    public List<Seat> getSeatsByScheduleId(String scheduleId) {
        return repository.findByScheduleId(scheduleId);
    }

    @Transactional
    public Seat updateSeat(Seat seat) {
        return repository.save(seat);
    }
}
