package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.repository.ConcertScheduleRepository;
import kr.hhplus.be.server.domain.concert.exception.ConcertScheduleErrorCode;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertScheduleServiceTest {

    @InjectMocks
    ConcertScheduleService concertScheduleService;

    @Mock
    ConcertScheduleRepository concertScheduleRepository;

    @Test
    void getSchedule_Success() {
        ConcertSchedule concertSchedule = ConcertSchedule.builder().id("123").concertId("asdf").build();
        when(concertScheduleRepository.findById("123")).thenReturn(Optional.of(concertSchedule));

        ConcertSchedule actual = concertScheduleService.getSchedule("123");

        assertThat(actual).isNotNull();
    }

    @Test
    void getSchedule_Fail() {
        when(concertScheduleRepository.findById("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> concertScheduleService.getSchedule("123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ConcertScheduleErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    void getSchedulesByConcertId_Success() {
        List<ConcertSchedule> schedules = List.of(ConcertSchedule.builder().id("1").build(), ConcertSchedule.builder().id("2").build());
        when(concertScheduleRepository.findByConcertId("1")).thenReturn(schedules);

        List<ConcertSchedule> actual = concertScheduleService.getSchedulesByConcertId("1");

        assertThat(actual).hasSize(2);
    }

    @Test
    void getSchedulesByConcertId_Empty(){
        List<ConcertSchedule> schedules = List.of();
        when(concertScheduleRepository.findByConcertId("1")).thenReturn(schedules);

        List<ConcertSchedule> actual = concertScheduleService.getSchedulesByConcertId("1");

        assertThat(actual).hasSize(0);
    }

}