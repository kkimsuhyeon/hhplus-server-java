package kr.hhplus.be.server.domain.concert.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "seats")
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seat_id")
    @Comment("좌석 아이디")
    private String id;

    @Column(name = "seat_number", nullable = false)
    @Comment("좌석 번호")
    private int seatNumber;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Comment("좌석 상태")
    private SeatStatus status;

    @Column(name = "price", nullable = false)
    @Comment("가격")
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ConcertScheduleEntity schedule;

    @OneToMany(mappedBy = "seat")
    @BatchSize(size = 10)
    private List<ReservationEntity> reservations;

    public boolean isReservable() {
        return this.status == SeatStatus.AVAILABLE;
    }

    public void addReservation(ReservationEntity reservation) {
        reservations.add(reservation);
    }

}
