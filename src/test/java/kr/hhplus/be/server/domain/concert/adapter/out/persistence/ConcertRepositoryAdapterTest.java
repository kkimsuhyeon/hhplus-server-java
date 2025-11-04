package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import kr.hhplus.be.server.config.TestJpaAuditingConfig;
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
@Import({ConcertRepositoryAdapter.class, TestJpaAuditingConfig.class})
class ConcertRepositoryAdapterTest {

    @Autowired
    private ConcertRepositoryAdapter concertRepositoryAdapter;

    @Autowired
    private TestEntityManager em;

    private ConcertEntity concert1;
    private ConcertScheduleEntity schedule1;
    private ConcertScheduleEntity schedule2;

    @BeforeEach
    void setUp() {
        concert1 = ConcertEntity.builder().title("concert1").description("concert1").build();

        em.persist(concert1);

        schedule1 = ConcertScheduleEntity.builder().concert(concert1).concertDate(LocalDateTime.now().plusDays(7)).build();
        schedule2 = ConcertScheduleEntity.builder().concert(concert1).concertDate(LocalDateTime.now().plusDays(14)).build();

        em.persist(schedule1);
        em.persist(schedule2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("ID로 콘서트 조회 - 성공")
    void findById_Success() {
        Optional<ConcertEntity> actual = concertRepositoryAdapter.findById(concert1.getId());

        assertThat(actual)
                .isPresent()
                .get()
                .extracting(ConcertEntity::getId, ConcertEntity::getTitle)
                .containsExactly(concert1.getId(), concert1.getTitle());

        assertThat(actual.get().getSchedules()).hasSize(2);
    }

    @Test
    @DisplayName("ID로 콘서트 조회 - 존재하지 않는 ID")
    void findById_NotFound() {
        Optional<ConcertEntity> result = concertRepositoryAdapter.findById("TEST");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("콘서트 저장 - 성공")
    void save_Success() {
        ConcertEntity expected = ConcertEntity.builder().title("new").description("concert").build();

        ConcertEntity actual = concertRepositoryAdapter.save(expected);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());

        em.flush();
        em.clear();

        Optional<ConcertEntity> actualData = concertRepositoryAdapter.findById(actual.getId());
        assertThat(actualData).isPresent();
        assertThat(actualData.get().getId()).isEqualTo(expected.getId());
    }

    @Test
    @DisplayName("조건으로 콘서트 목록 조회 - 제목으로 검색")
    void findAllByCriteria_WithTitle() {
        ConcertCriteria criteria = ConcertCriteria.builder().title("concert1").build();
        Pageable pageable = PageRequest.of(0, 10);

        Page<ConcertEntity> actual = concertRepositoryAdapter.findAllByCriteria(criteria, pageable);

        assertThat(actual.getContent()).hasSize(1);
        assertThat(actual.getContent().getFirst().getTitle()).contains("concert1");
    }

    @Test
    @DisplayName("조건으로 콘서트 목록 조회 - 전체 조회")
    void findAllByCriteria_All() {
        ConcertCriteria criteria = ConcertCriteria.builder().build();
        Pageable pageable = PageRequest.of(0, 10);

        Page<ConcertEntity> actual = concertRepositoryAdapter.findAllByCriteria(criteria, pageable);

        assertThat(actual.getContent()).hasSize(2);
        assertThat(actual.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("페이징 처리 확인")
    void findAllByCriteria_WithPaging() {
        for (int i = 3; i <= 15; i++) {
            ConcertEntity concert = ConcertEntity.builder().title("concert " + i).description("concert " + i).build();
            em.persist(concert);
        }

        em.flush();
        em.clear();

        ConcertCriteria criteria = ConcertCriteria.builder().build();
        Pageable firstPage = PageRequest.of(0, 5);
        Pageable secondPage = PageRequest.of(1, 5);

        // When
        Page<ConcertEntity> firstActual = concertRepositoryAdapter.findAllByCriteria(criteria, firstPage);
        Page<ConcertEntity> secondActual = concertRepositoryAdapter.findAllByCriteria(criteria, secondPage);

        // Then
        assertThat(firstActual.getContent()).hasSize(5);
        assertThat(firstActual.getTotalElements()).isEqualTo(15);
        assertThat(firstActual.getTotalPages()).isEqualTo(3);
        assertThat(firstActual.getNumber()).isEqualTo(0);

        assertThat(secondActual.getContent()).hasSize(5);
        assertThat(secondActual.getNumber()).isEqualTo(1);
    }
}