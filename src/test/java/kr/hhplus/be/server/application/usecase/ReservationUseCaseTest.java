package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatStatus;
import kr.hhplus.be.server.domain.concert.model.service.SeatService;
import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;
import kr.hhplus.be.server.domain.reservation.model.repository.ReservationRepository;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.model.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationUseCaseTest {

    @InjectMocks
    private ReservationUseCase reservationUseCase;

    @Mock
    private SeatService seatService;

    @Mock
    private UserService userService;

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("예약 - 성공")
    void reserve_success() {
        SeatEntity seatMock = Mockito.mock(SeatEntity.class);
        UserEntity userMock = Mockito.mock(UserEntity.class);

        when(seatMock.isReservable()).thenReturn(true);

        given(seatService.getSeat("1")).willReturn(seatMock);
        given(userService.getUser("1")).willReturn(userMock);
        given(reservationRepository.save(any(ReservationEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ReserveSeatCommand request = ReserveSeatCommand.builder().seatId("1").userId("1").build();
        reservationUseCase.execute(request);

        verify(seatService, times(1)).getSeat("1");
        verify(userService, times(1)).getUser("1");
        verify(reservationRepository, times(1)).save(any(ReservationEntity.class));
        verify(seatMock, times(1)).setStatus(SeatStatus.RESERVING);
    }

    @Test
    @DisplayName("예약 - 실패 (이미 예약된 좌석)")
    void reserve_fail() {
        SeatEntity seatMock = Mockito.mock(SeatEntity.class);

        when(seatMock.isReservable()).thenReturn(false);
        given(seatService.getSeat("1")).willReturn(seatMock);

        ReserveSeatCommand request = ReserveSeatCommand.builder().seatId("1").userId("1").build();

        assertThatThrownBy(() -> reservationUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약이 불가능한 좌석");
    }

}