package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.domain.concert.application.dto.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.application.mapper.ConcertMapper;
import kr.hhplus.be.server.domain.concert.application.dto.query.FindConcertQuery;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.factory.ConcertCriteriaFactory;
import kr.hhplus.be.server.domain.concert.model.repository.ConcertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConcertService 테스트")
class ConcertServiceImplTest {

    @InjectMocks
    private ConcertService concertService;

    @Mock
    private ConcertCriteriaFactory criteriaFactory;

    @Mock
    private ConcertMapper mapper;

    @Mock
    private ConcertRepository concertRepository;

    private Pageable pageable;
    private FindConcertQuery findQuery;
    private ConcertCriteria criteria;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        findQuery = FindConcertQuery.builder().build();
        criteria = ConcertCriteria.builder().build();
    }

    @Test
    @DisplayName("콘서트 목록 조회 - 성공")
    void getConcerts_Success() {
        ConcertEntity concert1 = ConcertEntity.builder().id("1").title("concert1").build();
        ConcertEntity concert2 = ConcertEntity.builder().id("2").title("concert2").build();

        List<ConcertEntity> expectedList = Arrays.asList(concert1, concert2);
        Page<ConcertEntity> expected = new PageImpl<>(expectedList, pageable, expectedList.size());

        given(criteriaFactory.fromQuery(findQuery)).willReturn(criteria);
        given(concertRepository.findAllByCriteria(criteria, pageable)).willReturn(expected);

        Page<ConcertEntity> actual = concertService.getConcerts(findQuery, pageable);

        assertThat(actual).isNotNull();
        assertThat(actual.getContent()).hasSize((int) expected.getTotalElements());
        assertThat(actual.getContent())
                .extracting("title")
                .containsExactly("concert1", "concert2");

        // 메서드 호출 검증
        verify(criteriaFactory, times(1)).fromQuery(findQuery);
        verify(concertRepository, times(1)).findAllByCriteria(criteria, pageable);
    }

    @Test
    @DisplayName("콘서트 목록 조회 - 빈 결과")
    void getConcerts_Empty() {
        Page<ConcertEntity> expected = new PageImpl<>(List.of(), pageable, 0);

        given(criteriaFactory.fromQuery(findQuery)).willReturn(criteria);
        given(concertRepository.findAllByCriteria(criteria, pageable)).willReturn(expected);

        Page<ConcertEntity> result = concertService.getConcerts(findQuery, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("콘서트 목록 조회 - 페이징 처리 확인")
    void getConcerts_WithPaging() {

        ConcertEntity concert3 = ConcertEntity.builder().id("3").title("콘서트3").build();
        ConcertEntity concert4 = ConcertEntity.builder().id("4").title("콘서트4").build();

        Pageable secondPage = PageRequest.of(1, 5);
        List<ConcertEntity> expectedList = Arrays.asList(concert3, concert4);
        Page<ConcertEntity> expected = new PageImpl<>(expectedList, secondPage, 12);

        given(criteriaFactory.fromQuery(findQuery)).willReturn(criteria);
        given(concertRepository.findAllByCriteria(criteria, secondPage)).willReturn(expected);

        Page<ConcertEntity> actual = concertService.getConcerts(findQuery, secondPage);

        assertThat(actual.getNumber()).isEqualTo(1);
        assertThat(actual.getSize()).isEqualTo(5);
        assertThat(actual.getTotalElements()).isEqualTo(12);
        assertThat(actual.getTotalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("콘서트 생성 - 성공")
    void save_Success() {
        given(mapper.toEntity(any(CreateConcertCommand.class))).willReturn(any(ConcertEntity.class));
        given(concertRepository.save(any(ConcertEntity.class))).willReturn(any(ConcertEntity.class));

        concertService.save(any(CreateConcertCommand.class));

        verify(mapper, times(1)).toEntity(any(CreateConcertCommand.class));
        verify(concertRepository, times(1)).save(any(ConcertEntity.class));
    }
}
