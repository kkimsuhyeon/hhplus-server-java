package kr.hhplus.be.server.domain.concert.adapter.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.concert.application.repository.ConcertRepository;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ConcertRepository의 JPA 구현체
 * JPA Entity와 순수 도메인 Model 간의 변환을 담당
 */
@Repository
@RequiredArgsConstructor
public class ConcertRepositoryAdapter implements ConcertRepository {

    private final ConcertJpaRepository jpaRepository;
    private final ConcertMapper mapper;

    @Override
    public Concert findById(String id) {
        ConcertEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

        return mapper.toDomain(entity);
    }

    @Override
    public List<Concert> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Concert save(Concert concert) {
        if (concert.getId() != null) {
            // 수정: 기존 Entity를 조회해서 업데이트
            ConcertEntity entity = jpaRepository.findById(concert.getId())
                    .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

            // 도메인 Model의 변경사항을 Entity에 반영
            mapper.updateEntity(entity, concert);

            // 더티 체킹으로 자동 저장됨
        } else {
            // 신규 생성은 현재 지원하지 않음 (기존 Entity와의 호환성 이슈)
            throw new UnsupportedOperationException("새 콘서트 생성은 아직 지원되지 않습니다.");
        }

        return concert;
    }
}
