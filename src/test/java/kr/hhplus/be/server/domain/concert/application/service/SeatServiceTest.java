package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.repository.SeatRepository;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @InjectMocks
    private SeatService seatService;

    @Mock
    private SeatRepository seatRepository;

    @Test
    void getSeat_Success() {
        Seat expect = Seat.builder().id("123").build();
        when(seatRepository.findById("123")).thenReturn(Optional.of(expect));

        Seat actual = seatService.getSeat("123");

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(expect.getId());
    }

    @Test
    void getSeat_Fail() {
        when(seatRepository.findById("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.getSeat("123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(SeatErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    void getSeatsByScheduleId_Success() {
        Seat expected1 = Seat.builder().build();
        Seat expected2 = Seat.builder().build();

        when(seatRepository.findByScheduleId("123")).thenReturn(List.of(expected1, expected2));

        List<Seat> actual = seatService.getSeatsByScheduleId("123");

        assertThat(actual).hasSize(2);
    }

    @Test
    void getSeatsByScheduleId_Empty() {
        when(seatRepository.findByScheduleId("123")).thenReturn(List.of());

        List<Seat> actual = seatService.getSeatsByScheduleId("123");

        assertThat(actual).isEmpty();
    }

    @Test
    void reserve_Success() {
        Seat expect = Seat.builder().status(SeatStatus.AVAILABLE).build();
        when(seatRepository.findByIdForUpdate("123")).thenReturn(Optional.of(expect));

        seatService.reserve("123", "user1");

        ArgumentCaptor<Seat> seatArgumentCaptor = ArgumentCaptor.forClass(Seat.class);
        verify(seatRepository, times(1)).update(seatArgumentCaptor.capture());

        Seat actual = seatArgumentCaptor.getValue();
        assertThat(actual.getStatus()).isEqualTo(SeatStatus.RESERVING);
        assertThat(actual.getUserId()).isEqualTo("user1");
    }

    @Test
    void reserve_Fail() {
        when(seatRepository.findByIdForUpdate("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.reserve("123", "user1"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(SeatErrorCode.NOT_FOUND.getMessage());

    }
}