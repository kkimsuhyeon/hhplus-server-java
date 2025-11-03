package kr.hhplus.be.server.domain.concert.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ConcertEntityTest {

    @Test
    @DisplayName("예약 가능 스케쥴이 있는 경우")
    void isReservable_true() {
        List<ConcertScheduleEntity> actualSchedule = new ArrayList<>();
        actualSchedule.add(createReservableSchedule());
        ConcertEntity actual = ConcertEntity.builder()
                .concertSchedules(actualSchedule)
                .build();

        assertThat(actual.isReservable()).isTrue();
    }

    @Test
    @DisplayName("예약 가능 스케쥴이 없는 경우")
    void isReservable_false() {
        List<ConcertScheduleEntity> actualSchedule = new ArrayList<>();
        actualSchedule.add(createNonReservableSchedule());
        ConcertEntity actual = ConcertEntity.builder()
                .concertSchedules(actualSchedule)
                .build();

        assertThat(actual.isReservable()).isFalse();
    }

    @Test
    @DisplayName("스케쥴이 없는 경우")
    void isReservable_empty() {
        List<ConcertScheduleEntity> actualSchedule = new ArrayList<>();
        ConcertEntity actual = ConcertEntity.builder()
                .concertSchedules(actualSchedule)
                .build();

        assertThat(actual.isReservable()).isFalse();
    }

    @Test
    @DisplayName("예약 가능 스케쥴이 포함되어 있는 경우")
    void isReservable_contain() {
        ConcertEntity actual = createConcertWithSchedule(true, false, false);

        assertThat(actual.isReservable()).isTrue();
    }

    private ConcertEntity createConcertWithSchedule(boolean... reservableFlags) {
        List<ConcertScheduleEntity> schedules = new ArrayList<>();

        for (boolean isReservable : reservableFlags) {
            ConcertScheduleEntity concertScheduleEntity = isReservable ? createReservableSchedule() : createNonReservableSchedule();
            schedules.add(concertScheduleEntity);
        }

        return ConcertEntity.builder()
                .concertSchedules(schedules)
                .build();
    }

    private ConcertScheduleEntity createReservableSchedule() {
        ConcertScheduleEntity mock = Mockito.mock(ConcertScheduleEntity.class);
        when(mock.isReservable()).thenReturn(true);

        return mock;
    }

    private ConcertScheduleEntity createNonReservableSchedule() {
        ConcertScheduleEntity mock = Mockito.mock(ConcertScheduleEntity.class);
        when(mock.isReservable()).thenReturn(false);

        return mock;
    }
}
