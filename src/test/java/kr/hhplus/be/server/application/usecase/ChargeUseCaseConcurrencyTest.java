package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ChargeCommand;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.application.service.UserCommandService;
import kr.hhplus.be.server.domain.user.application.service.UserQueryService;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ChargeUseCaseConcurrencyTest {

    @Autowired
    private ChargeUseCase chargeUseCase;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserQueryService userQueryService;

    @Test
    void 동시에_같은_키로_충전해도_한번만_반영되고_모두_성공한다() throws InterruptedException {
        User user = createUser();
        UUID idempotencyKey = UUID.randomUUID();

        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        Queue<Throwable> errors = new ConcurrentLinkedQueue<>();

        ChargeCommand command = ChargeCommand.builder()
                .userId(user.getId())
                .amount(BigDecimal.valueOf(1000))
                .build();

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                ready.countDown();
                try {
                    start.await();
                    chargeUseCase.charge(command, idempotencyKey);
                } catch (InterruptedException ignored) {
                } catch (Exception e) {
                    errors.add(e);
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        User actual = userQueryService.getUser(user.getId());
        assertThat(errors).isEmpty();
        assertThat(actual.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
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
