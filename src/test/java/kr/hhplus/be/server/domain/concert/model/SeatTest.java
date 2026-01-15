package kr.hhplus.be.server.domain.concert.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SeatTest {

    @Test
    void isReservable_Test() {
        Seat availableSeat = Seat.builder().status(SeatStatus.AVAILABLE).build();
        Seat reservedSeat = Seat.builder().status(SeatStatus.RESERVED).build();

        assertThat(availableSeat.isReservable()).isTrue();
        assertThat(reservedSeat.isReservable()).isFalse();
    }

    @Test
    void reserve_Success() {
        Seat availableSeat = Seat.builder().status(SeatStatus.AVAILABLE).build();
        availableSeat.reserve("user");

        assertThat(availableSeat.getStatus()).isEqualTo(SeatStatus.RESERVING);
        assertThat(availableSeat.getUserId()).isEqualTo("user");
        assertThat(availableSeat.getHoldExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void reserve_Fail() {
        Seat availableSeat = Seat.builder().status(SeatStatus.RESERVING).build();

        assertThatThrownBy(() -> availableSeat.reserve("user"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(SeatErrorCode.ALREADY_RESERVED.getMessage());
    }
}