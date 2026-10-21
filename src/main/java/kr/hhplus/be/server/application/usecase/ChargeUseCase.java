package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ChargeCommand;
import kr.hhplus.be.server.domain.user.application.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChargeUseCase {

    private final UserCommandService userCommandService;

    public void charge(ChargeCommand command) {
        userCommandService.addBalance(command.getUserId(), command.getAmount());
    }
}
