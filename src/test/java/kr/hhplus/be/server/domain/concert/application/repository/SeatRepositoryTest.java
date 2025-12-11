package kr.hhplus.be.server.domain.concert.application.repository;

import kr.hhplus.be.server.domain.concert.adapter.out.persistence.adapter.SeatRepositoryAdapter;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.SeatJpaRepository;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SeatRepositoryAdapter.class})
class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatJpaRepository jpaRepository;

//    @Test
//    void findByIdForUpdate() {
//        SeatEntity.
//    }

//    @Test
//    void 동시에_같은좌석_예약시_하나만_성공() throws InterruptedException {
//        SeatEntity availableSeat = SeatEntity.builder().status(SeatStatus.AVAILABLE).build();
//        SeatEntity seat = jpaRepository.save(availableSeat);
//
//        int threadCount = 10;
//        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//        AtomicInteger successCount = new AtomicInteger(0);
//
//        // when - 10개 스레드가 "동시에" 예약 시도
//        for (int i = 0; i < threadCount; i++) {
//            String userId = "user-" + i;
//            executor.submit(() -> {
//                try {
//                    seatService.reserve(seat.getId(),
//                            userId);
//                    successCount.incrementAndGet();
//                } catch (Exception e) {
//                    // 실패
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        // then - 정확히 1명만 성공해야 함
//        assertThat(successCount.get()).isEqualTo(1);
//    }
}