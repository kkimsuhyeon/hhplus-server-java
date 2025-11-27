package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.domain.user.application.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceImplTest {

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("유저 조회 - 성공")
    void getUser_Success() {
        userService.getUser("123");

    }

    @Test
    @DisplayName("유저 조회 - 실패")
    void getUser_Fail() {


    }

    @Test
    @DisplayName("유저 생성")
    void createUser() {

    }

}