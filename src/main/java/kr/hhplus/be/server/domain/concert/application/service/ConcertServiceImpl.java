package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.domain.concert.application.port.out.ConcertRepository;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 콘서트 서비스 구현체
 */
@Service
@Transactional(readOnly = true)
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertServiceImpl(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    @Override
    public ConcertEntity getConcert(String concertId) {
        if (concertId == null || concertId.isBlank()) {
            throw new IllegalArgumentException("콘서트 ID는 필수입니다.");
        }

        return concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다. ID: " + concertId));
    }

    @Override
    public List<ConcertEntity> getAllConcerts() {
        return concertRepository.findAll();
    }

    @Override
    public List<ConcertScheduleEntity> getAvailableSchedules(String concertId) {
        if (concertId == null || concertId.isBlank()) {
            throw new IllegalArgumentException("콘서트 ID는 필수입니다.");
        }

        // 콘서트 존재 여부 확인
        ConcertEntity concert = getConcert(concertId);

        // 현재 시간 이후의 스케줄만 조회
        LocalDateTime now = LocalDateTime.now();
        return concertRepository.findAvailableSchedules(concertId, now);
    }

    @Override
    public List<SeatEntity> getAvailableSeats(String scheduleId) {
        if (scheduleId == null || scheduleId.isBlank()) {
            throw new IllegalArgumentException("스케줄 ID는 필수입니다.");
        }

        // 스케줄 존재 여부 확인
        ConcertScheduleEntity schedule = concertRepository.findScheduleById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다. ID: " + scheduleId));

        return concertRepository.findAvailableSeats(scheduleId);
    }

    @Override
    @Transactional
    public SeatEntity reserveSeat(String seatId, String userId) {
        if (seatId == null || seatId.isBlank()) {
            throw new IllegalArgumentException("좌석 ID는 필수입니다.");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        SeatEntity seat = concertRepository.findSeatById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다. ID: " + seatId));

        // 좌석 상태 확인 (실제로는 SeatEntity에 메서드가 필요하지만, 여기서는 직접 체크)
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 예약된 좌석입니다. 좌석 ID: " + seatId);
        }

        // 좌석 예약 처리 (실제로는 SeatEntity에 메서드가 필요)
        seat.reserve(userId);

        return concertRepository.saveSeat(seat);
    }
}
