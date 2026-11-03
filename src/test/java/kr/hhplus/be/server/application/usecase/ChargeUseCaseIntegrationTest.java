package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ChargeCommand;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.requestkey.exception.RequestKeyErrorCode;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.application.service.UserCommandService;
import kr.hhplus.be.server.domain.user.application.service.UserQueryService;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ChargeUseCaseIntegrationTest {

    @Autowired
    private ChargeUseCase chargeUseCase;

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private UserCommandService userCommandService;

    @Test
    void 같은_키로_두번_충전해도_잔액은_한번만_늘어난다() {
        User user = createUser();
        UUID idempotencyKey = UUID.randomUUID();

        ChargeCommand command = ChargeCommand.builder()
                .userId(user.getId())
                .amount(new BigDecimal("1000"))
                .build();

        chargeUseCase.charge(command, idempotencyKey);
        chargeUseCase.charge(command, idempotencyKey);

        User actual = userQueryService.getUser(user.getId());
        assertThat(actual.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void 같은_키에_다른_금액이면_KEY_REUSED이고_잔액은_그대로다() {
        User user = createUser();
        UUID idempotencyKey = UUID.randomUUID();

        ChargeCommand command1 = ChargeCommand.builder()
                .userId(user.getId())
                .amount(new BigDecimal("1000"))
                .build();

        ChargeCommand command2 = ChargeCommand.builder()
                .userId(user.getId())
                .amount(new BigDecimal("2000"))
                .build();

        chargeUseCase.charge(command1, idempotencyKey);
        assertThatThrownBy(() -> chargeUseCase.charge(command2, idempotencyKey))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(RequestKeyErrorCode.KEY_REUSED);

        User actual = userQueryService.getUser(user.getId());
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
