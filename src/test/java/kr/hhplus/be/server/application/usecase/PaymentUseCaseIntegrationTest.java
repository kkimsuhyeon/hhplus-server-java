package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateScheduleCommand;
import kr.hhplus.be.server.domain.concert.application.repository.SeatRepository;
import kr.hhplus.be.server.domain.concert.application.service.ConcertScheduleService;
import kr.hhplus.be.server.domain.concert.application.service.ConcertService;
import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import kr.hhplus.be.server.domain.payment.exception.PaymentErrorCode;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.reservation.application.CreateReservationCommand;
import kr.hhplus.be.server.domain.reservation.application.ReservationService;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.application.UserService;
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
class PaymentUseCaseIntegrationTest {

    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private SeatService seatService;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertScheduleService concertScheduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Test
    void 결제_정상() {
        User user = createUser(BigDecimal.valueOf(1000));
        Seat seat = createReservedSeat(BigDecimal.valueOf(500), user.getId(), LocalDateTime.now().plusMinutes(5));
        Reservation reservation = createReservation(user.getId(), seat.getId(), seat.getPrice());

        PayCommand command = PayCommand.builder().userId(user.getId()).reservationId(reservation.getId()).build();
        Payment payment = paymentUseCase.pay(command);

        User actualUser = userService.getUser(user.getId());
        Seat actualSeat = seatService.getSeat(seat.getId());
        Reservation actualReservation = reservationService.getReservation(reservation.getId());

        assertThat(actualUser.getBalance()).isEqualTo(BigDecimal.valueOf(500));
        assertThat(actualSeat.getStatus()).isEqualTo(SeatStatus.RESERVED);
        assertThat(actualReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void 중복_결제() {
        User user = createUser(BigDecimal.valueOf(1000));
        Seat seat = createReservedSeat(BigDecimal.valueOf(500), user.getId(), LocalDateTime.now().plusMinutes(5));
        Reservation reservation = createReservation(user.getId(), seat.getId(), seat.getPrice());

        PayCommand command = PayCommand.builder().userId(user.getId()).reservationId(reservation.getId()).build();

        paymentUseCase.pay(command);
        assertThatThrownBy(() -> paymentUseCase.pay(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ReservationErrorCode.ALREADY_PAID.getMessage());

        User actualUser = userService.getUser(user.getId());
        Seat actualSeat = seatService.getSeat(seat.getId());
        Reservation actualReservation = reservationService.getReservation(reservation.getId());

        assertThat(actualUser.getBalance()).isEqualTo(BigDecimal.valueOf(500));
        assertThat(actualSeat.getStatus()).isEqualTo(SeatStatus.RESERVED);
        assertThat(actualReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void 취소된_결제_시도() {

    }

    private Reservation createReservation(String userId, String seatId, BigDecimal price) {
        CreateReservationCommand command = CreateReservationCommand.builder()
                .userId(userId)
                .seatId(seatId)
                .price(price)
                .build();

        return reservationService.create(command);
    }

    private User createUser(BigDecimal balance) {
        User user = User.builder()
                .balance(balance)
                .build();

        return userRepository.save(user);
    }

    private Seat createReservedSeat(BigDecimal price, String userId, LocalDateTime time) {
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

        Seat seat = Seat.builder().status(SeatStatus.RESERVING).price(price).scheduleId(schedule.getId()).userId(userId).holdExpiresAt(time).build();
        return seatRepository.save(seat);
    }

}