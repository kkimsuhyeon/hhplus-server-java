package kr.hhplus.be.server.domain.concert.application;

import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {

    private final ConcertScheduleRepository repository;

    @Transactional(readOnly = true)
    public ConcertSchedule getSchedule(String scheduleId) {
        return repository.findById(scheduleId);
    }

    @Transactional(readOnly = true)
    public List<ConcertSchedule> getSchedulesByConcertId(String concertId) {
        return repository.findByConcertId(concertId);
    }

    @Transactional
    public ConcertSchedule createSchedule(ConcertSchedule schedule) {
        return repository.save(schedule);
    }
}
