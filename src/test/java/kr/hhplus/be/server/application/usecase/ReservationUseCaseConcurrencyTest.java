package kr.hhplus.be.server.application.usecase;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateScheduleCommand;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateSeatCommand;
import kr.hhplus.be.server.domain.concert.application.service.ConcertScheduleService;
import kr.hhplus.be.server.domain.concert.application.service.ConcertService;
import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.application.UserService;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationUseCaseConcurrencyTest {

    @Autowired
    private ReservationUseCase reservationUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertScheduleService concertScheduleService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 한유저가_두번_같은_좌석_예약() throws InterruptedException {

        User user = createUser();
        Seat seat = createSeat(BigDecimal.valueOf(500));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ReserveSeatCommand command = ReserveSeatCommand.builder()
                .userId(user.getId())
                .seatId(seat.getId())
                .build();

        for (int i = 0; i < 2; i++) {
            executor.submit(() -> {
                try {
                    reservationUseCase.reserve(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }

    @Test
    void 여러유저가_같은_좌석_예약() throws InterruptedException {
        User user1 = createUser();
        User user2 = createUser();
        User user3 = createUser();
        User user4 = createUser();

        List<User> users = List.of(user1, user2, user3, user4);

        Seat seat = createSeat(BigDecimal.valueOf(500));

        ExecutorService executor = Executors.newFixedThreadPool(4);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        List<CompletableFuture<Void>> futures = IntStream.range(0, 4)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        ReserveSeatCommand command = ReserveSeatCommand.builder()
                                .userId(users.get(i).getId())
                                .seatId(seat.getId())
                                .build();
                        reservationUseCase.reserve(command);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                }, executor)).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        assertThat(failCount.get()).isEqualTo(3);
        assertThat(successCount.get()).isEqualTo(1);
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
