package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;
import kr.hhplus.be.server.domain.reservation.model.service.ReservationService;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentUseCaseTest {

    @InjectMocks
    private PaymentUseCase paymentUseCase;

    @Mock
    private ReservationService reservationService;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("결제 - 성공")
    void pay_success() {
        ReservationEntity reservationMock = mock(ReservationEntity.class);
        UserEntity userMock = mock(UserEntity.class);
        SeatEntity seatMock = mock(SeatEntity.class);

        when(reservationService.getReservation("1")).thenReturn(reservationMock);
        when(userService.getUser("1")).thenReturn(userMock);
        when(reservationMock.getSeat()).thenReturn(seatMock);
        when(seatMock.getPrice()).thenReturn(BigInteger.valueOf(1000));

        PayCommand command = PayCommand.builder()
                .reservationId("1")
                .userId("1")
                .build();

        paymentUseCase.pay(command);

        verify(reservationMock, times(1)).validateForPayment("1");
        verify(userMock, times(1)).deductBalance(BigInteger.valueOf(1000));
        verify(reservationMock, times(1)).completePayment();
    }

    @Test
    @DisplayName("결제 - 실패(예약 못찾음)")
    void pay_fail_reservationNotFound() {
        when(reservationService.getReservation("test"))
                .thenThrow(new BusinessException(ReservationErrorCode.NOT_FOUND));

        PayCommand command = PayCommand.builder()
                .reservationId("test")
                .build();

        assertThatThrownBy(() -> paymentUseCase.pay(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ReservationErrorCode.NOT_FOUND.getMessage());

        verify(userService, never()).getUser(any());
    }

    @Test
    @DisplayName("결제 - 실패(소유자가 아님)")
    void pay_fail_noOwner() {
        ReservationEntity reservationMock = mock(ReservationEntity.class);
        UserEntity userMock = mock(UserEntity.class);

        when(reservationService.getReservation("test"))
                .thenReturn(reservationMock);

        doThrow(new BusinessException(CommonErrorCode.FORBIDDEN_ERROR))
                .when(reservationMock)
                .validateForPayment("test");

        PayCommand command = PayCommand.builder()
                .reservationId("test")
                .userId("test")
                .build();

        assertThatThrownBy(() -> paymentUseCase.pay(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CommonErrorCode.FORBIDDEN_ERROR.getMessage());

        verify(userMock, never()).deductBalance(any());
        verify(reservationMock, never()).completePayment();
    }

    @Test
    @DisplayName("결제 - 실패(포인트 부족)")
    void pay_fail_noPoint() {

        ReservationEntity reservationMock = mock(ReservationEntity.class);
        UserEntity userMock = mock(UserEntity.class);
        SeatEntity seatEntity = mock(SeatEntity.class);

        when(reservationService.getReservation("1")).thenReturn(reservationMock);
        when(userService.getUser("1")).thenReturn(userMock);
        when(reservationMock.getSeat()).thenReturn(seatEntity);
        when(seatEntity.getPrice()).thenReturn(BigInteger.valueOf(1000));

        doThrow(new BusinessException(UserErrorCode.NOT_ENOUGH_POINT))
                .when(userMock)
                .deductBalance(BigInteger.valueOf(1000));

        PayCommand command = PayCommand.builder()
                .reservationId("1")
                .userId("1")
                .build();

        assertThatThrownBy(() -> paymentUseCase.pay(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.NOT_ENOUGH_POINT.getMessage());

        verify(reservationMock, times(1)).validateForPayment("1");
        verify(reservationMock, never()).completePayment();
    }


}