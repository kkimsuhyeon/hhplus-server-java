package kr.hhplus.be.server.application.usecase;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.domain.concert.application.service.ConcertScheduleService;
import kr.hhplus.be.server.domain.concert.application.service.ConcertService;
import kr.hhplus.be.server.domain.concert.application.service.SeatService;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class ReservationUseCaseConcurrencyTest {

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
    }

    @Test
    void 한유저가_두번_같은_좌석_예약() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ReserveSeatCommand command = ReserveSeatCommand.builder()
                .userId(users.getFirst().getId())
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
}
