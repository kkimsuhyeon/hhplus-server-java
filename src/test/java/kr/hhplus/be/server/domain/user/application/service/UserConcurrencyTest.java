package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("User 동시성 테스트")
class UserConcurrencyTest {

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserQueryService userQueryService;

    @Test
    @DisplayName("[비관적 락] 동시에 N번 충전해도 lost update 없이 전부 반영된다")
    void addBalance_concurrent() throws InterruptedException {
        // given
        User user = createUser();
        int threadCount = 10;
        BigDecimal amount = BigDecimal.valueOf(100);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        // when — 모든 스레드를 동시에 출발시킨다
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    userCommandService.addBalance(user.getId(), amount);
                } catch (InterruptedException ignored) {
                } finally {
                    done.countDown();
                }
            });
        }
        ready.await();
        start.countDown();
        done.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // then — 비관적 락이 직렬화 → 100 * 10 = 1000 전부 반영
        User actual = userQueryService.getUser(user.getId());
        assertThat(actual.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("[낙관적 락] 동시에 이름을 수정하면 하나만 성공하고 나머지는 충돌한다")
    void changeName_concurrent() throws InterruptedException {
        // given
        User user = createUser();
        int threadCount = 2;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        // when — 같은 유저를 동시에 수정
        for (int i = 0; i < threadCount; i++) {
            final String newName = "name" + i;
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    userCommandService.changeName(user.getId(), newName);
                    success.incrementAndGet();
                } catch (InterruptedException ignored) {
                } catch (Exception e) {
                    fail.incrementAndGet();   // ObjectOptimisticLockingFailureException
                } finally {
                    done.countDown();
                }
            });
        }
        ready.await();
        start.countDown();
        done.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // then — @Version이 충돌 감지 → 1개만 성공
        assertThat(success.get()).isEqualTo(1);
        assertThat(fail.get()).isEqualTo(1);
    }

    private User createUser() {
        CreateUserCommand command = CreateUserCommand.builder()
                .email("test" + System.nanoTime() + "@test.com")
                .password("password123")
                .name("origin")
                .build();
        return userCommandService.create(command);
    }
}
