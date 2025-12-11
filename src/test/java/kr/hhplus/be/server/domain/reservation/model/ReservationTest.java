package kr.hhplus.be.server.domain.reservation.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    @Test
    void isExpired_Test() {
        Reservation expiredReservation = Reservation.builder().expireAt(LocalDateTime.now().minusMinutes(1)).build();
        Reservation validReservation = Reservation.builder().expireAt(LocalDateTime.now().plusMinutes(1)).build();

        assertThat(expiredReservation.isExpired()).isTrue();
        assertThat(validReservation.isExpired()).isFalse();
    }

    @Test
    void isPayable_Test() {
        Reservation payableReservation = Reservation.builder().status(ReservationStatus.PENDING_PAYMENT).build();
        Reservation canceledReservation = Reservation.builder().status(ReservationStatus.CANCELLED).build();

        assertThat(payableReservation.isPayable()).isTrue();
        assertThat(canceledReservation.isPayable()).isFalse();
    }

    @Test
    void isOwnedBy_Test() {
        Reservation reservation = Reservation.builder().userId("1").build();

        assertThat(reservation.isOwnedBy("1")).isTrue();
        assertThat(reservation.isOwnedBy("123")).isFalse();
    }

    @Test
    void complete_Success() {
        // given
        Reservation reservation = Reservation.builder().status(ReservationStatus.PENDING_PAYMENT).build();

        // when
        reservation.completePayment();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void complete_Fail() {
        // given
        Reservation reservation = Reservation.builder().status(ReservationStatus.CANCELLED).build();

        // when, then
        assertThatThrownBy(reservation::completePayment)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ReservationErrorCode.NOT_PAYABLE.getMessage());
    }

}