package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;

import java.util.List;

/**
 * 콘서트 도메인 서비스
 */
public interface ConcertService {

    /**
     * 콘서트 ID로 콘서트를 조회합니다.
     *
     * @param concertId 콘서트 ID
     * @return 콘서트 엔티티
     * @throws IllegalArgumentException 콘서트를 찾을 수 없는 경우
     */
    ConcertEntity getConcert(String concertId);

    /**
     * 모든 콘서트를 조회합니다.
     *
     * @return 콘서트 목록
     */
    List<ConcertEntity> getAllConcerts();

    /**
     * 콘서트의 예약 가능한 스케줄을 조회합니다.
     *
     * @param concertId 콘서트 ID
     * @return 예약 가능한 스케줄 목록
     * @throws IllegalArgumentException 콘서트를 찾을 수 없는 경우
     */
    List<ConcertScheduleEntity> getAvailableSchedules(String concertId);

    /**
     * 특정 스케줄의 예약 가능한 좌석을 조회합니다.
     *
     * @param scheduleId 스케줄 ID
     * @return 예약 가능한 좌석 목록
     * @throws IllegalArgumentException 스케줄을 찾을 수 없는 경우
     */
    List<SeatEntity> getAvailableSeats(String scheduleId);

    /**
     * 좌석을 예약합니다.
     *
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 예약된 좌석
     * @throws IllegalArgumentException 좌석을 찾을 수 없거나 이미 예약된 경우
     */
    SeatEntity reserveSeat(String seatId, String userId);
}
