package kr.hhplus.be.server.domain.reservation.application;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;

    @Mock
    ReservationRepository reservationRepository;

    @Test
    @DisplayName("예약 조회 - 성공")
    void getReservation_Success() {
        Reservation expectedReservation = Reservation.builder().id("123").build();
        when(reservationRepository.findById("123")).thenReturn(Optional.of(expectedReservation));

        Reservation actualReservation = reservationService.getReservation("123");

        assertThat(actualReservation).isNotNull();
        assertThat(actualReservation.getId()).isEqualTo(expectedReservation.getId());
    }

    @Test
    @DisplayName("예약 조회 - 실패")
    void getReservation_Fail() {
        when(reservationRepository.findById("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getReservation("123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ReservationErrorCode.NOT_FOUND.getMessage());
    }

}