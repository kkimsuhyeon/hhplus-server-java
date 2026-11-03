package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ChargeCommand;
import kr.hhplus.be.server.domain.requestkey.application.service.RequestKeyCommandService;
import kr.hhplus.be.server.domain.requestkey.model.RequestKey;
import kr.hhplus.be.server.domain.user.application.service.UserCommandService;
import kr.hhplus.be.server.shared.hash.HashUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChargeUseCase {

    private final UserCommandService userCommandService;
    private final RequestKeyCommandService requestKeyCommandService;

    public void charge(ChargeCommand command, UUID idempotencyKey) {
        String hash = HashUtils.sha256(command.getAmount().stripTrailingZeros().toPlainString());
        RequestKey requestKey = RequestKey.of("CHARGE", hash);

        userCommandService.lockForUpdate(command.getUserId());

        if (requestKeyCommandService.isRetry(command.getUserId(), idempotencyKey, requestKey)) {
            return;
        }

        userCommandService.addBalance(command.getUserId(), command.getAmount());
        requestKeyCommandService.save(command.getUserId(), idempotencyKey, requestKey);
    }

}
