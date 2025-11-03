package kr.hhplus.be.server.domain.concert.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ConcertScheduleEntityTest {

    @Test
    @DisplayName("예약 가능 좌석이 있는 경우")
    void isReservable_true() {
        ConcertScheduleEntity actual = createScheduleWithSeat(true);

        assertThat(actual.isReservable()).isTrue();
        assertThat(actual.getReservableSeatCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("예약 가능 좌석이 없는 경우")
    void isReservable_false() {
        ConcertScheduleEntity actual = createScheduleWithSeat(false);

        assertThat(actual.isReservable()).isFalse();
        assertThat(actual.getReservableSeatCount()).isZero();
    }

    @Test
    @DisplayName("좌석이 없는 경우")
    void isReservable_empty() {
        ConcertScheduleEntity actual = ConcertScheduleEntity.builder()
                .seats(List.of())
                .build();

        assertThat(actual.isReservable()).isFalse();
        assertThat(actual.getReservableSeatCount()).isZero();
    }

    @Test
    @DisplayName("예약 가능 좌석이 포함되어 있는 경우")
    void isReservable_contain() {
        ConcertScheduleEntity actual = createScheduleWithSeat(true, false, false, true);

        assertThat(actual.isReservable()).isTrue();
        assertThat(actual.getReservableSeatCount()).isEqualTo(2L);
    }

    private ConcertScheduleEntity createScheduleWithSeat(boolean... reservableFlags) {
        List<SeatEntity> seats = new ArrayList<>();

        for (boolean isReservable : reservableFlags) {
            SeatEntity entity = isReservable ? createReservableSeat() : createNonReservableSeat();
            seats.add(entity);
        }

        return ConcertScheduleEntity.builder()
                .seats(seats)
                .build();
    }

    private SeatEntity createReservableSeat() {
        SeatEntity mock = Mockito.mock(SeatEntity.class);
        when(mock.isReservable()).thenReturn(true);

        return mock;
    }

    private SeatEntity createNonReservableSeat() {
        SeatEntity mock = Mockito.mock(SeatEntity.class);
        when(mock.isReservable()).thenReturn(false);

        return mock;
    }

}