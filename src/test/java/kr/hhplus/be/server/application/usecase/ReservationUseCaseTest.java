package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.domain.concert.application.service.ConcertScheduleService;
import kr.hhplus.be.server.domain.concert.application.service.ConcertService;
import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.user.adapter.out.persistence.UserJpaRepository;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.application.UserService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ReservationUseCaseTest {

    @Autowired
    private ReservationUseCase reservationUseCase;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertScheduleService concertScheduleService;

    @Autowired
    private SeatService seatService;


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
        seat = seatService.create(Seat.builder().scheduleId(schedule.getId()).price(BigDecimal.valueOf(1000)).build());

        Page<User> allByCriteria = userRepository.findAllByCriteria(null, PageRequest.of(0, 10));
        users.addAll(allByCriteria.getContent());
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
    }

    @Test
    void 테스트123() {

//        ExecutorService executor = Executors.newFixedThreadPool(10);
//        CountDownLatch latch = new CountDownLatch(10);
//        AtomicInteger successCount = new AtomicInteger(0);
//
//        for (int i = 0; i < 10; i++) {
//            executor.submit(() -> {
//                ReserveSeatCommand command = ReserveSeatCommand.builder()
//                        .userId("user1")
//                        .seatId("seat1")
//                        .build();
//
//                reservationUseCase.reserve(command);
//            })
//        }

    }

}