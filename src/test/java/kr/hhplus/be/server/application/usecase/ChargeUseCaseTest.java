package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ChargeCommand;
import kr.hhplus.be.server.domain.requestkey.application.service.RequestKeyCommandService;
import kr.hhplus.be.server.domain.requestkey.model.RequestKey;
import kr.hhplus.be.server.domain.user.application.service.UserCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChargeUseCase 테스트")
class ChargeUseCaseTest {

    private static final String USER_ID = "userId";
    private static final UUID IDEMPOTENCY_KEY = new UUID(0L, 1L);

    @InjectMocks
    private ChargeUseCase chargeUseCase;

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private RequestKeyCommandService requestKeyCommandService;


    @Test
    void 재시도면_충전하지_않는다() {
        when(requestKeyCommandService.isRetry(eq(USER_ID), eq(IDEMPOTENCY_KEY), any(RequestKey.class))).thenReturn(true);

        ChargeCommand command = ChargeCommand.builder()
                .userId(USER_ID)
                .amount(BigDecimal.valueOf(1000))
                .build();

        chargeUseCase.charge(command, IDEMPOTENCY_KEY);

        InOrder inOrder = inOrder(userCommandService, requestKeyCommandService);
        inOrder.verify(userCommandService).lockForUpdate(USER_ID);
        inOrder.verify(requestKeyCommandService).isRetry(eq(USER_ID), eq(IDEMPOTENCY_KEY), any(RequestKey.class));
        verify(userCommandService, never()).addBalance(any(), any());
        verify(requestKeyCommandService, never()).save(any(), any(), any());
    }

    @Test
    void 처음_요청이면_충전하고_키를_저장한다() {
        when(requestKeyCommandService.isRetry(eq(USER_ID), eq(IDEMPOTENCY_KEY), any(RequestKey.class))).thenReturn(false);

        ChargeCommand command = ChargeCommand.builder()
                .userId(USER_ID)
                .amount(BigDecimal.valueOf(1000))
                .build();

        chargeUseCase.charge(command, IDEMPOTENCY_KEY);

        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        InOrder inOrder = inOrder(userCommandService, requestKeyCommandService);
        inOrder.verify(userCommandService).lockForUpdate(eq(USER_ID));
        inOrder.verify(requestKeyCommandService).isRetry(eq(USER_ID), eq(IDEMPOTENCY_KEY), any(RequestKey.class));
        inOrder.verify(userCommandService).addBalance(eq(USER_ID), amountCaptor.capture());
        inOrder.verify(requestKeyCommandService).save(eq(USER_ID), eq(IDEMPOTENCY_KEY), any(RequestKey.class));

        assertThat(amountCaptor.getValue()).isEqualByComparingTo("1000");
    }

    @Test
    void 금액_표기가_달라도_같은_해시를_만든다() {
        ChargeCommand command1 = ChargeCommand.builder()
                .userId(USER_ID)
                .amount(new BigDecimal("1000"))
                .build();

        ChargeCommand command2 = ChargeCommand.builder()
                .userId(USER_ID)
                .amount(new BigDecimal("1000.00"))
                .build();

        chargeUseCase.charge(command1, IDEMPOTENCY_KEY);
        chargeUseCase.charge(command2, IDEMPOTENCY_KEY);

        ArgumentCaptor<RequestKey> requestKeyCaptor = ArgumentCaptor.forClass(RequestKey.class);

        verify(requestKeyCommandService, times(2)).isRetry(eq(USER_ID), eq(IDEMPOTENCY_KEY), requestKeyCaptor.capture());
        List<RequestKey> keys = requestKeyCaptor.getAllValues();
        assertThat(keys.get(0).getPayloadHash()).isEqualTo(keys.get(1).getPayloadHash());
    }

}