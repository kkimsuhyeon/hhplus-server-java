package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigInteger;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "seats")
public class SeatJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seat_id")
    @Comment("좌석 아이디")
    private String id;

    @Column(name = "seat_number", nullable = false)
    @Comment("좌석 번호")
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Comment("좌석 상태")
    private SeatStatus status;

    @Column(name = "price", nullable = false)
    @Comment("가격")
    private BigInteger price;

    @Column(name = "schedule_id", nullable = false)
    @Comment("스케줄 아이디")
    private String scheduleId;

    public static SeatJpaEntity create(Seat seat) {
        return SeatJpaEntity.builder()
                .seatNumber(seat.getSeatNumber())
                .status(seat.getStatus())
                .price(seat.getPrice())
                .scheduleId(seat.getScheduleId())
                .build();
    }

    public Seat toModel() {
        return Seat.builder()
                .id(this.id)
                .seatNumber(this.seatNumber)
                .status(this.status)
                .price(this.price)
                .scheduleId(this.scheduleId)
                .build();
    }

    public void update(Seat seat) {
        this.status = seat.getStatus();
        this.price = seat.getPrice();
    }
}
