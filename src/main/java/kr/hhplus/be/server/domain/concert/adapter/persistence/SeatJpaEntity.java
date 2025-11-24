package kr.hhplus.be.server.domain.concert.adapter.persistence;

import jakarta.persistence.*;
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

    void updateFromDomain(SeatStatus status) {
        this.status = status;
    }
}
