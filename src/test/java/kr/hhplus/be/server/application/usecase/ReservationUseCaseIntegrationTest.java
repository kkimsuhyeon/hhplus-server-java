package kr.hhplus.be.server.application.usecase;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
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
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ReservationUseCaseIntegrationTest {

    @Autowired
    private ReservationUseCase reservationUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertScheduleService concertScheduleService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private EntityManager entityManager;

    private List<User> users = new ArrayList<>();
    private Concert concert;
    private ConcertSchedule schedule;
    private Seat seat;

    @BeforeEach
    public void setUp() {
        User user1 = User.builder().balance(BigDecimal.ZERO).build();
        User user2 = User.builder().balance(BigDecimal.ZERO).build();
        User user3 = User.builder().balance(BigDecimal.ZERO).build();
        User user4 = User.builder().balance(BigDecimal.ZERO).build();
        User user5 = User.builder().balance(BigDecimal.ZERO).build();
        User user6 = User.builder().balance(BigDecimal.ZERO).build();
        User user7 = User.builder().balance(BigDecimal.ZERO).build();
        User user8 = User.builder().balance(BigDecimal.ZERO).build();
        User user9 = User.builder().balance(BigDecimal.ZERO).build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);
        userRepository.save(user9);

        concert = concertService.save(Concert.builder().title("title").build());
        schedule = concertScheduleService.create(ConcertSchedule.builder().concertId(concert.getId()).concertDate(LocalDateTime.now()).build());
        seat = seatService.save(Seat.builder().status(SeatStatus.AVAILABLE).scheduleId(schedule.getId()).price(BigDecimal.valueOf(1000)).build());

        Page<User> allByCriteria = userRepository.findAllByCriteria(null, PageRequest.of(0, 10));
        users.addAll(allByCriteria.getContent());

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void 생성확인() {
        assertThat(concert).isNotNull();
        assertThat(schedule).isNotNull();
        assertThat(seat).isNotNull();
        assertThat(users.size()).isEqualTo(9);
    }

    @Test
    void 예약_정상처리() {
        User user = users.getFirst();
        ReserveSeatCommand command = ReserveSeatCommand.builder()
                .userId(user.getId())
                .seatId(seat.getId())
                .build();

        Reservation reservation = reservationUseCase.reserve(command);

        entityManager.flush();
        entityManager.clear();

        assertThat(reservation).isNotNull();
        assertThat(reservation.getUserId()).isEqualTo(user.getId());
        assertThat(reservation.getSeatId()).isEqualTo(seat.getId());
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING_PAYMENT);

        Seat updatedSeat = seatService.getSeat(reservation.getSeatId());
        assertThat(updatedSeat.getStatus()).isEqualTo(SeatStatus.RESERVING);
    }

    @Test
    void 이미_예약된_좌석_예약시_에러발생() {
        User user = users.getFirst();
        ReserveSeatCommand command = ReserveSeatCommand.builder()
                .userId(user.getId())
                .seatId(seat.getId())
                .build();

        reservationUseCase.reserve(command);

        assertThatThrownBy(() -> reservationUseCase.reserve(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(SeatErrorCode.ALREADY_RESERVED.getMessage());
        ;


    }

}