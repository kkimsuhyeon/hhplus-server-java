package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateScheduleCommand;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateSeatCommand;
import kr.hhplus.be.server.domain.concert.application.service.ConcertScheduleService;
import kr.hhplus.be.server.domain.concert.application.service.ConcertService;
import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.application.UserService;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ReservationUseCaseIntegrationTest {

    @Autowired
    private ReservationUseCase reservationUseCase;

    @Autowired
    private UserService userService;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertScheduleService concertScheduleService;

    @Autowired
    private SeatService seatService;

    @Test
    void 예약_정상처리() {
        User user = createUser();
        Seat seat = createSeat(BigDecimal.valueOf(1000));

        ReserveSeatCommand command = ReserveSeatCommand.builder()
                .userId(user.getId())
                .seatId(seat.getId())
                .build();

        Reservation reservation = reservationUseCase.reserve(command);

        assertThat(reservation).isNotNull();
        assertThat(reservation.getUserId()).isEqualTo(user.getId());
        assertThat(reservation.getSeatId()).isEqualTo(seat.getId());
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING_PAYMENT);

        Seat updatedSeat = seatService.getSeat(reservation.getSeatId());
        assertThat(updatedSeat.getStatus()).isEqualTo(SeatStatus.RESERVING);
    }

    @Test
    void 이미_예약된_좌석_예약시_에러발생() {

        User user = createUser();
        Seat seat = createSeat(BigDecimal.valueOf(1000));

        ReserveSeatCommand command = ReserveSeatCommand.builder()
                .userId(user.getId())
                .seatId(seat.getId())
                .build();

        reservationUseCase.reserve(command);
        assertThatThrownBy(() -> reservationUseCase.reserve(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(SeatErrorCode.ALREADY_RESERVED.getMessage());
    }

    private User createUser() {
        CreateUserCommand command = CreateUserCommand.builder().build();
        return userService.create(command);
    }

    private Seat createSeat(BigDecimal price) {
        CreateConcertCommand createConcertCommand = CreateConcertCommand.builder()
                .title("title")
                .description("desc")
                .build();

        Concert concert = concertService.create(createConcertCommand);

        CreateScheduleCommand createScheduleCommand = CreateScheduleCommand.builder()
                .concertId(concert.getId())
                .concertDate(LocalDateTime.now())
                .build();

        ConcertSchedule schedule = concertScheduleService.create(createScheduleCommand);

        CreateSeatCommand createSeatCommand = CreateSeatCommand.builder()
                .price(price)
                .scheduleId(schedule.getId())
                .build();

        return seatService.create(createSeatCommand);
    }
}