package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.domain.concert.model.repository.ConcertCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ConcertRepositoryAdapter.class) // ✅ Adapter를 테스트 컨텍스트에 추가
@DisplayName("ConcertRepositoryAdapter 테스트")
class ConcertRepositoryAdapterTest {

    @Autowired
    private ConcertRepositoryAdapter concertRepositoryAdapter;

    @Autowired
    private TestEntityManager em;

    private ConcertEntity concert1;
    private ConcertEntity concert2;
    private ConcertScheduleEntity schedule1;
    private ConcertScheduleEntity schedule2;

    @BeforeEach
    void setUp() {
        // Given: 테스트 데이터 준비
        concert1 = ConcertEntity.builder()
                .title("아이유 콘서트")
                .description("아이유 콘서트 설명")
                .build();

        concert2 = ConcertEntity.builder()
                .title("BTS 콘서트")
                .description("BTS 콘서트 설명")
                .build();

        em.persist(concert1);
        em.persist(concert2);

        schedule1 = ConcertScheduleEntity.builder()
                .concert(concert1)
                .concertDate(LocalDateTime.now().plusDays(7))
                .build();

        schedule2 = ConcertScheduleEntity.builder()
                .concert(concert1)
                .concertDate(LocalDateTime.now().plusDays(14))
                .build();

        em.persist(schedule1);
        em.persist(schedule2);

        em.flush();
        em.clear(); // 영속성 컨텍스트 초기화
    }

    @Test
    @DisplayName("ID로 콘서트 조회 - 성공")
    void findById_Success() {
        // When
        Optional<ConcertEntity> result = concertRepositoryAdapter.findById(concert1.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("아이유 콘서트");
        assertThat(result.get().getDescription()).isEqualTo("아이유 콘서트 설명");
    }

    @Test
    @DisplayName("ID로 콘서트 조회 - 존재하지 않는 ID")
    void findById_NotFound() {
        // When
        Optional<ConcertEntity> result = concertRepositoryAdapter.findById("non-existent-id");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("콘서트 저장 - 성공")
    void save_Success() {
        // Given
        ConcertEntity newConcert = ConcertEntity.builder()
                .title("새로운 콘서트")
                .description("새로운 콘서트 설명")
                .build();

        // When
        ConcertEntity saved = concertRepositoryAdapter.save(newConcert);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("새로운 콘서트");

        // DB에 실제로 저장되었는지 확인
        em.flush();
        em.clear();

        Optional<ConcertEntity> found = concertRepositoryAdapter.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("새로운 콘서트");
    }

    @Test
    @DisplayName("조건으로 콘서트 목록 조회 - 제목으로 검색")
    void findAllByCriteria_WithTitle() {
        // Given
        ConcertCriteria criteria = ConcertCriteria.builder()
                .title("아이유")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ConcertEntity> result = concertRepositoryAdapter.findAllByCriteria(criteria, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("아이유");
    }

    @Test
    @DisplayName("조건으로 콘서트 목록 조회 - 전체 조회")
    void findAllByCriteria_All() {
        // Given
        ConcertCriteria criteria = ConcertCriteria.builder().build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ConcertEntity> result = concertRepositoryAdapter.findAllByCriteria(criteria, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("페이징 처리 확인")
    void findAllByCriteria_WithPaging() {
        // Given: 추가 데이터 생성
        for (int i = 3; i <= 15; i++) {
            ConcertEntity concert = ConcertEntity.builder()
                    .title("콘서트 " + i)
                    .description("설명 " + i)
                    .build();
            em.persist(concert);
        }
        em.flush();
        em.clear();

        ConcertCriteria criteria = ConcertCriteria.builder().build();
        Pageable firstPage = PageRequest.of(0, 5);
        Pageable secondPage = PageRequest.of(1, 5);

        // When
        Page<ConcertEntity> firstResult = concertRepositoryAdapter.findAllByCriteria(criteria, firstPage);
        Page<ConcertEntity> secondResult = concertRepositoryAdapter.findAllByCriteria(criteria, secondPage);

        // Then
        assertThat(firstResult.getContent()).hasSize(5);
        assertThat(firstResult.getTotalElements()).isEqualTo(15);
        assertThat(firstResult.getTotalPages()).isEqualTo(3);
        assertThat(firstResult.getNumber()).isEqualTo(0);

        assertThat(secondResult.getContent()).hasSize(5);
        assertThat(secondResult.getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("LAZY 로딩 확인 - schedules는 자동으로 로드되지 않음")
    void lazyLoading_Schedules() {
        // When
        Optional<ConcertEntity> result = concertRepositoryAdapter.findById(concert1.getId());

        // Then
        assertThat(result).isPresent();
        ConcertEntity concert = result.get();

        // 영속성 컨텍스트를 clear하지 않은 상태에서 schedules에 접근
        // LAZY 로딩이므로 접근 시점에 쿼리가 실행됨
        assertThat(concert.getConcertSchedules()).hasSize(2);
    }

    @Test
    @DisplayName("연관 엔티티 포함 저장 - cascade 없이")
    void save_WithSchedule() {
        // Given
        ConcertEntity newConcert = ConcertEntity.builder()
                .title("테스트 콘서트")
                .description("테스트 설명")
                .build();

        // When: Concert만 저장
        ConcertEntity savedConcert = concertRepositoryAdapter.save(newConcert);
        em.flush();

        // Schedule은 별도로 저장해야 함
        ConcertScheduleEntity newSchedule = ConcertScheduleEntity.builder()
                .concert(savedConcert)
                .concertDate(LocalDateTime.now().plusDays(30))
                .build();
        em.persist(newSchedule);
        em.flush();
        em.clear();

        // Then
        Optional<ConcertEntity> found = concertRepositoryAdapter.findById(savedConcert.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getConcertSchedules()).hasSize(1);
    }
}