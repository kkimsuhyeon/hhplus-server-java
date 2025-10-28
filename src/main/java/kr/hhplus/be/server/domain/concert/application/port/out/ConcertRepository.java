package kr.hhplus.be.server.domain.concert.application.port.out;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 콘서트 도메인의 영속성 포트
 */
public interface ConcertRepository {

    /**
     * 콘서트 ID로 콘서트를 조회합니다.
     *
     * @param concertId 콘서트 ID
     * @return 콘서트 엔티티
     */
    Optional<ConcertEntity> findById(String concertId);

    /**
     * 모든 콘서트를 조회합니다.
     *
     * @return 콘서트 목록
     */
    List<ConcertEntity> findAll();

    /**
     * 콘서트의 예약 가능한 스케줄을 조회합니다.
     *
     * @param concertId 콘서트 ID
     * @param fromDate 조회 시작 날짜
     * @return 콘서트 스케줄 목록
     */
    List<ConcertScheduleEntity> findAvailableSchedules(String concertId, LocalDateTime fromDate);

    /**
     * 스케줄 ID로 스케줄을 조회합니다.
     *
     * @param scheduleId 스케줄 ID
     * @return 콘서트 스케줄
     */
    Optional<ConcertScheduleEntity> findScheduleById(String scheduleId);

    /**
     * 특정 스케줄의 예약 가능한 좌석을 조회합니다.
     *
     * @param scheduleId 스케줄 ID
     * @return 좌석 목록
     */
    List<SeatEntity> findAvailableSeats(String scheduleId);

    /**
     * 좌석 ID로 좌석을 조회합니다.
     *
     * @param seatId 좌석 ID
     * @return 좌석 엔티티
     */
    Optional<SeatEntity> findSeatById(String seatId);

    /**
     * 좌석을 저장합니다.
     *
     * @param seat 좌석 엔티티
     * @return 저장된 좌석
     */
    SeatEntity saveSeat(SeatEntity seat);
}
