package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.repository.ConcertScheduleRepository;
import kr.hhplus.be.server.domain.concert.exception.ConcertScheduleErrorCode;
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
        return repository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ConcertScheduleErrorCode.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ConcertSchedule> getSchedulesByConcertId(String concertId) {
        return repository.findByConcertId(concertId);
    }

    @Transactional
    public ConcertSchedule create(ConcertSchedule schedule) {
        return repository.save(schedule);
    }

    @Transactional
    public ConcertSchedule update(ConcertSchedule schedule) {
        return repository.update(schedule);
    }

}
