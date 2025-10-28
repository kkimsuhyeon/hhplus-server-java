package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.domain.concert.application.port.out.ConcertRepository;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * ConcertServiceImpl 테스트
 *
 * 이 테스트는 AssertJ를 활용한 다양한 검증 방법을 보여줍니다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConcertService 테스트")
class ConcertServiceImplTest {

    @Mock
    private ConcertRepository concertRepository;

    @InjectMocks
    private ConcertServiceImpl concertService;

    private ConcertEntity concert;
    private ConcertScheduleEntity schedule;
    private SeatEntity seat;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        concert = ConcertEntity.builder()
                .id("concert-1")
                .title("BTS 콘서트")
                .description("BTS 월드투어 서울 공연")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        schedule = ConcertScheduleEntity.builder()
                .id("schedule-1")
                .concertDate(LocalDateTime.now().plusDays(7))
                .concert(concert)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        seat = SeatEntity.builder()
                .id("seat-1")
                .seatNumber(1)
                .status(SeatStatus.AVAILABLE)
                .price(150000L)
                .schedule(schedule)
                .build();
    }

    @Nested
    @DisplayName("getConcert 메서드는")
    class GetConcertTest {

        @Test
        @DisplayName("유효한 ID로 콘서트를 조회할 수 있다")
        void getConcert_WithValidId_ReturnsConcert() {
            // given
            given(concertRepository.findById("concert-1"))
                    .willReturn(Optional.of(concert));

            // when
            ConcertEntity result = concertService.getConcert("concert-1");

            // then - AssertJ 기본 검증
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("concert-1");
            assertThat(result.getTitle()).isEqualTo("BTS 콘서트");

            // AssertJ - 객체 필드 검증
            assertThat(result)
                    .extracting("id", "title", "description")
                    .containsExactly("concert-1", "BTS 콘서트", "BTS 월드투어 서울 공연");

            // Mockito 검증
            then(concertRepository).should(times(1)).findById("concert-1");
        }

        @Test
        @DisplayName("null ID로 조회 시 예외가 발생한다")
        void getConcert_WithNullId_ThrowsException() {
            // when & then - AssertJ Exception 검증
            assertThatThrownBy(() -> concertService.getConcert(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("콘서트 ID는 필수입니다.");

            // Repository 호출되지 않음을 검증
            then(concertRepository).should(never()).findById(any());
        }

        @Test
        @DisplayName("빈 문자열 ID로 조회 시 예외가 발생한다")
        void getConcert_WithBlankId_ThrowsException() {
            // when & then - 다양한 AssertJ Exception 검증 방법
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> concertService.getConcert("   "))
                    .withMessageContaining("콘서트 ID는 필수입니다.");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getConcert_WithNonExistentId_ThrowsException() {
            // given
            given(concertRepository.findById("non-existent"))
                    .willReturn(Optional.empty());

            // when & then - AssertJ Exception 검증 (메시지 포함)
            assertThatThrownBy(() -> concertService.getConcert("non-existent"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("콘서트를 찾을 수 없습니다")
                    .hasMessageContaining("non-existent");
        }
    }

    @Nested
    @DisplayName("getAllConcerts 메서드는")
    class GetAllConcertsTest {

        @Test
        @DisplayName("모든 콘서트 목록을 조회할 수 있다")
        void getAllConcerts_ReturnsAllConcerts() {
            // given
            ConcertEntity concert2 = ConcertEntity.builder()
                    .id("concert-2")
                    .title("아이유 콘서트")
                    .description("아이유 단독 콘서트")
                    .build();

            List<ConcertEntity> concerts = Arrays.asList(concert, concert2);
            given(concertRepository.findAll()).willReturn(concerts);

            // when
            List<ConcertEntity> result = concertService.getAllConcerts();

            // then - AssertJ 컬렉션 검증
            assertThat(result)
                    .isNotNull()
                    .hasSize(2)
                    .containsExactly(concert, concert2);

            // AssertJ - 컬렉션 요소 검증
            assertThat(result)
                    .extracting("id")
                    .containsExactlyInAnyOrder("concert-1", "concert-2");

            // AssertJ - 필터링 검증
            assertThat(result)
                    .filteredOn(c -> c.getTitle().contains("BTS"))
                    .hasSize(1)
                    .first()
                    .extracting("id")
                    .isEqualTo("concert-1");
        }

        @Test
        @DisplayName("콘서트가 없을 때 빈 리스트를 반환한다")
        void getAllConcerts_WhenNoConcerts_ReturnsEmptyList() {
            // given
            given(concertRepository.findAll()).willReturn(Collections.emptyList());

            // when
            List<ConcertEntity> result = concertService.getAllConcerts();

            // then - AssertJ 빈 컬렉션 검증
            assertThat(result)
                    .isNotNull()
                    .isEmpty()
                    .hasSize(0);
        }
    }

    @Nested
    @DisplayName("getAvailableSchedules 메서드는")
    class GetAvailableSchedulesTest {

        @Test
        @DisplayName("예약 가능한 스케줄 목록을 조회할 수 있다")
        void getAvailableSchedules_WithValidConcertId_ReturnsSchedules() {
            // given
            ConcertScheduleEntity schedule2 = ConcertScheduleEntity.builder()
                    .id("schedule-2")
                    .concertDate(LocalDateTime.now().plusDays(14))
                    .concert(concert)
                    .build();

            List<ConcertScheduleEntity> schedules = Arrays.asList(schedule, schedule2);

            given(concertRepository.findById("concert-1"))
                    .willReturn(Optional.of(concert));
            given(concertRepository.findAvailableSchedules(eq("concert-1"), any(LocalDateTime.class)))
                    .willReturn(schedules);

            // when
            List<ConcertScheduleEntity> result = concertService.getAvailableSchedules("concert-1");

            // then - AssertJ 컬렉션 상세 검증
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(2)
                    .allMatch(s -> s.getConcert().getId().equals("concert-1"))
                    .allSatisfy(s -> {
                        assertThat(s.getId()).isNotNull();
                        assertThat(s.getConcertDate()).isAfter(LocalDateTime.now());
                    });

            // AssertJ - 날짜 검증
            assertThat(result)
                    .extracting("concertDate")
                    .allMatch(date -> ((LocalDateTime) date).isAfter(LocalDateTime.now()));
        }

        @Test
        @DisplayName("존재하지 않는 콘서트 ID로 조회 시 예외가 발생한다")
        void getAvailableSchedules_WithNonExistentConcertId_ThrowsException() {
            // given
            given(concertRepository.findById("non-existent"))
                    .willReturn(Optional.empty());

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> concertService.getAvailableSchedules("non-existent"))
                    .withMessageContaining("콘서트를 찾을 수 없습니다");

            // 스케줄 조회는 호출되지 않음
            then(concertRepository).should(never())
                    .findAvailableSchedules(anyString(), any(LocalDateTime.class));
        }
    }

    @Nested
    @DisplayName("getAvailableSeats 메서드는")
    class GetAvailableSeatsTest {

        @Test
        @DisplayName("예약 가능한 좌석 목록을 조회할 수 있다")
        void getAvailableSeats_WithValidScheduleId_ReturnsSeats() {
            // given
            SeatEntity seat2 = SeatEntity.builder()
                    .id("seat-2")
                    .seatNumber(2)
                    .status(SeatStatus.AVAILABLE)
                    .price(150000L)
                    .schedule(schedule)
                    .build();

            List<SeatEntity> seats = Arrays.asList(seat, seat2);

            given(concertRepository.findScheduleById("schedule-1"))
                    .willReturn(Optional.of(schedule));
            given(concertRepository.findAvailableSeats("schedule-1"))
                    .willReturn(seats);

            // when
            List<SeatEntity> result = concertService.getAvailableSeats("schedule-1");

            // then - AssertJ 컬렉션 고급 검증
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(2)
                    .allMatch(SeatEntity::isAvailable)
                    .extracting("seatNumber", "status", "price")
                    .containsExactlyInAnyOrder(
                            tuple(1, SeatStatus.AVAILABLE, 150000L),
                            tuple(2, SeatStatus.AVAILABLE, 150000L)
                    );

            // AssertJ - 조건 검증
            assertThat(result)
                    .filteredOn("status", SeatStatus.AVAILABLE)
                    .hasSize(2);
        }

        @Test
        @DisplayName("좌석이 없을 때 빈 리스트를 반환한다")
        void getAvailableSeats_WhenNoSeats_ReturnsEmptyList() {
            // given
            given(concertRepository.findScheduleById("schedule-1"))
                    .willReturn(Optional.of(schedule));
            given(concertRepository.findAvailableSeats("schedule-1"))
                    .willReturn(Collections.emptyList());

            // when
            List<SeatEntity> result = concertService.getAvailableSeats("schedule-1");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("reserveSeat 메서드는")
    class ReserveSeatTest {

        @Test
        @DisplayName("예약 가능한 좌석을 예약할 수 있다")
        void reserveSeat_WithAvailableSeat_ReservesSuccessfully() {
            // given
            String userId = "user-1";
            SeatEntity reservedSeat = SeatEntity.builder()
                    .id("seat-1")
                    .seatNumber(1)
                    .status(SeatStatus.RESERVING)
                    .price(150000L)
                    .schedule(schedule)
                    .build();

            given(concertRepository.findSeatById("seat-1"))
                    .willReturn(Optional.of(seat));
            given(concertRepository.saveSeat(any(SeatEntity.class)))
                    .willReturn(reservedSeat);

            // when
            SeatEntity result = concertService.reserveSeat("seat-1", userId);

            // then - AssertJ 객체 상태 검증
            assertThat(result)
                    .isNotNull()
                    .satisfies(s -> {
                        assertThat(s.getStatus()).isEqualTo(SeatStatus.RESERVING);
                        assertThat(s.isAvailable()).isFalse();
                    });

            // AssertJ - 메서드 체이닝 검증
            assertThat(result.getStatus())
                    .isNotEqualTo(SeatStatus.AVAILABLE)
                    .isIn(SeatStatus.RESERVING, SeatStatus.RESERVED);

            // Mockito ArgumentCaptor와 함께 사용
            then(concertRepository).should(times(1)).saveSeat(any(SeatEntity.class));
        }

        @Test
        @DisplayName("이미 예약된 좌석은 예약할 수 없다")
        void reserveSeat_WithReservedSeat_ThrowsException() {
            // given
            SeatEntity reservedSeat = SeatEntity.builder()
                    .id("seat-1")
                    .seatNumber(1)
                    .status(SeatStatus.RESERVED)
                    .price(150000L)
                    .schedule(schedule)
                    .build();

            given(concertRepository.findSeatById("seat-1"))
                    .willReturn(Optional.of(reservedSeat));

            // when & then - AssertJ Exception 상세 검증
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> concertService.reserveSeat("seat-1", "user-1"))
                    .withMessageContaining("예약 가능한 좌석이 아닙니다");

            // 저장 메서드는 호출되지 않음
            then(concertRepository).should(never()).saveSeat(any());
        }

        @Test
        @DisplayName("null 파라미터로 예약 시 예외가 발생한다")
        void reserveSeat_WithNullParameters_ThrowsException() {
            // when & then - 복합 검증
            assertAll(
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> concertService.reserveSeat(null, "user-1"))
                            .withMessageContaining("좌석 ID는 필수입니다"),

                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> concertService.reserveSeat("seat-1", null))
                            .withMessageContaining("사용자 ID는 필수입니다"),

                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> concertService.reserveSeat("", "user-1"))
                            .withMessageContaining("좌석 ID는 필수입니다")
            );
        }

        @Test
        @DisplayName("존재하지 않는 좌석 ID로 예약 시 예외가 발생한다")
        void reserveSeat_WithNonExistentSeatId_ThrowsException() {
            // given
            given(concertRepository.findSeatById("non-existent"))
                    .willReturn(Optional.empty());

            // when & then - AssertJ Code 검증
            assertThatCode(() -> concertService.reserveSeat("non-existent", "user-1"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("좌석을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("AssertJ 학습 예제")
    class AssertJLearningTest {

        @Test
        @DisplayName("문자열 검증 예제")
        void stringAssertionsExample() {
            String title = concert.getTitle();

            assertThat(title)
                    .isNotNull()
                    .isNotEmpty()
                    .isNotBlank()
                    .startsWith("BTS")
                    .endsWith("콘서트")
                    .contains("BTS")
                    .hasSize(8);
        }

        @Test
        @DisplayName("숫자 검증 예제")
        void numberAssertionsExample() {
            Long price = seat.getPrice();

            assertThat(price)
                    .isNotNull()
                    .isPositive()
                    .isGreaterThan(100000L)
                    .isLessThan(200000L)
                    .isBetween(100000L, 200000L);
        }

        @Test
        @DisplayName("날짜 검증 예제")
        void dateAssertionsExample() {
            LocalDateTime concertDate = schedule.getConcertDate();

            assertThat(concertDate)
                    .isNotNull()
                    .isAfter(LocalDateTime.now())
                    .isBefore(LocalDateTime.now().plusMonths(1));
        }

        @Test
        @DisplayName("Enum 검증 예제")
        void enumAssertionsExample() {
            SeatStatus status = seat.getStatus();

            assertThat(status)
                    .isNotNull()
                    .isEqualTo(SeatStatus.AVAILABLE)
                    .isIn(SeatStatus.AVAILABLE, SeatStatus.RESERVING)
                    .isNotEqualTo(SeatStatus.RESERVED);
        }

        @Test
        @DisplayName("컬렉션 다양한 검증 예제")
        void collectionAssertionsExample() {
            List<SeatEntity> seats = Arrays.asList(
                    seat,
                    SeatEntity.builder().id("seat-2").seatNumber(2)
                            .status(SeatStatus.AVAILABLE).price(150000L).build(),
                    SeatEntity.builder().id("seat-3").seatNumber(3)
                            .status(SeatStatus.RESERVED).price(150000L).build()
            );

            // 기본 컬렉션 검증
            assertThat(seats)
                    .isNotEmpty()
                    .hasSize(3)
                    .hasSizeGreaterThan(2)
                    .hasSizeLessThan(5);

            // 요소 포함 검증
            assertThat(seats)
                    .contains(seat)
                    .doesNotContainNull();

            // 필터링과 매칭
            assertThat(seats)
                    .filteredOn(SeatEntity::isAvailable)
                    .hasSize(2);

            assertThat(seats)
                    .anySatisfy(s -> assertThat(s.getStatus()).isEqualTo(SeatStatus.RESERVED))
                    .noneSatisfy(s -> assertThat(s.getPrice()).isNegative());
        }

        @Test
        @DisplayName("객체 필드 추출 검증 예제")
        void extractingFieldsExample() {
            List<ConcertEntity> concerts = Arrays.asList(
                    concert,
                    ConcertEntity.builder()
                            .id("concert-2")
                            .title("아이유 콘서트")
                            .description("아이유 단독 콘서트")
                            .build()
            );

            // 단일 필드 추출
            assertThat(concerts)
                    .extracting("title")
                    .containsExactly("BTS 콘서트", "아이유 콘서트");

            // 여러 필드 추출
            assertThat(concerts)
                    .extracting("id", "title")
                    .containsExactly(
                            tuple("concert-1", "BTS 콘서트"),
                            tuple("concert-2", "아이유 콘서트")
                    );

            // 메서드 참조로 추출
            assertThat(concerts)
                    .extracting(ConcertEntity::getId, ConcertEntity::getTitle)
                    .containsExactly(
                            tuple("concert-1", "BTS 콘서트"),
                            tuple("concert-2", "아이유 콘서트")
                    );
        }
    }
}
