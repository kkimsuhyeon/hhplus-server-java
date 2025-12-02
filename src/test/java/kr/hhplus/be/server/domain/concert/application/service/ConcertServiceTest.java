package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.dto.criteria.ConcertCriteria;
import kr.hhplus.be.server.domain.concert.application.dto.query.FindConcertQuery;
import kr.hhplus.be.server.domain.concert.application.repository.ConcertRepository;
import kr.hhplus.be.server.domain.concert.exception.ConcertErrorCode;
import kr.hhplus.be.server.domain.concert.model.Concert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @InjectMocks
    ConcertService concertService;

    @Mock
    ConcertRepository concertRepository;

    @Test
    @DisplayName("콘서트 조회 - 성공")
    void getConcert_Success() {
        Concert expectedConcert = Concert.builder().id("123").title("title").build();
        when(concertRepository.findById("123")).thenReturn(Optional.of(expectedConcert));

        Concert actualConcert = concertService.getConcert("123");

        assertThat(actualConcert).isNotNull();
        assertThat(actualConcert.getId()).isEqualTo(expectedConcert.getId());
    }

    @Test
    @DisplayName("콘서트 조회 - 실패")
    void getConcert_Fail() {
        when(concertRepository.findById("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> concertService.getConcert("123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ConcertErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("콘서트 목록 조회 - 성공")
    void getConcerts_Success() {
        // given
        FindConcertQuery query = FindConcertQuery.builder().title("123").build();
        Pageable pageable = PageRequest.of(0, 10);
        List<Concert> concerts = List.of(
                Concert.builder().id("1").title("123").build(),
                Concert.builder().id("2").title("123").build());
        Page<Concert> expectedPage = new PageImpl<>(concerts, pageable, concerts.size());

        when(concertRepository.findAllByCriteria(any(ConcertCriteria.class), eq(pageable)))
                .thenReturn(expectedPage);

        // when
        Page<Concert> actual = concertService.getConcerts(query, pageable);

        // then
        ArgumentCaptor<ConcertCriteria> criteriaCaptor = ArgumentCaptor.forClass(ConcertCriteria.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(concertRepository, times(1))
                .findAllByCriteria(criteriaCaptor.capture(), pageableCaptor.capture());

        ConcertCriteria criteriaCaptorValue = criteriaCaptor.getValue();
        Pageable pageableCaptorValue = pageableCaptor.getValue();
        assertThat(criteriaCaptorValue.getTitle()).isEqualTo("123");
        assertThat(pageableCaptorValue.getPageNumber()).isEqualTo(0);
        assertThat(pageableCaptorValue.getPageSize()).isEqualTo(10);

        assertThat(actual.getContent()).hasSize(2);
        assertThat(actual.getTotalElements()).isEqualTo(2);
    }

    @Test
    void getConcerts_Empty() {
        FindConcertQuery query = FindConcertQuery.builder().build();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Concert> expected = Page.empty();
        when(concertRepository.findAllByCriteria(any(ConcertCriteria.class), eq(pageable))).thenReturn(expected);

        Page<Concert> actual = concertService.getConcerts(query, pageable);

        assertThat(actual.getContent()).hasSize(0);
        assertThat(actual.getTotalElements()).isZero();
    }

    @Test
    void getConcerts_paging() {
        FindConcertQuery query = FindConcertQuery.builder().build();
        Pageable pageable = PageRequest.of(1, 5);
        List<Concert> concerts = List.of(
                Concert.builder().id("1").title("123").build(),
                Concert.builder().id("2").title("123").build());
        Page<Concert> expected = new PageImpl<>(concerts, pageable, 12);

        when(concertRepository.findAllByCriteria(any(ConcertCriteria.class), eq(pageable))).thenReturn(expected);

        Page<Concert> actual = concertService.getConcerts(query, pageable);

        ArgumentCaptor<ConcertCriteria> criteriaCaptor = ArgumentCaptor.forClass(ConcertCriteria.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(concertRepository, times(1)).findAllByCriteria(criteriaCaptor.capture(), pageableCaptor.capture());
        Pageable pageableCaptorValue = pageableCaptor.getValue();

        assertThat(pageableCaptorValue.getPageNumber()).isEqualTo(1);
        assertThat(pageableCaptorValue.getPageSize()).isEqualTo(5);

        assertThat(actual.getNumber()).isEqualTo(1);
        assertThat(actual.getTotalPages()).isEqualTo(3);
    }

}