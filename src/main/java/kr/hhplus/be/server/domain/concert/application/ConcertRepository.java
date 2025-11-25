package kr.hhplus.be.server.domain.concert.application;

import kr.hhplus.be.server.domain.concert.model.Concert;

import java.util.List;

/**
 * Concert Repository 인터페이스
 * 순수 도메인 모델(Concert)만 다루며, JPA에 대해 전혀 알지 못함
 */
public interface ConcertRepository {

    /**
     * ID로 콘서트 조회
     */
    Concert findById(String id);

    /**
     * 모든 콘서트 조회
     */
    List<Concert> findAll();

    /**
     * 콘서트 저장 (생성 및 수정)
     */
    Concert save(Concert concert);
}
