package kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "seats")
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seat_id")
    @Comment("좌석 아이디")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Comment("좌석 상태")
    private SeatStatus status;

    @Column(name = "price", nullable = false)
    @Comment("가격")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private ConcertScheduleEntity schedule;

    public static SeatEntity create(Seat seat, ConcertScheduleEntity schedule) {
        return SeatEntity.builder()
                .status(seat.getStatus())
                .price(seat.getPrice())
                .schedule(schedule)
                .build();
    }

    public Seat toModel() {
        return Seat.builder()
                .id(this.id)
                .status(this.status)
                .price(this.price)
                .scheduleId(this.schedule.getId())
                .build();
    }

    public void update(Seat seat) {
        this.status = seat.getStatus();
        this.price = seat.getPrice();
    }
}
