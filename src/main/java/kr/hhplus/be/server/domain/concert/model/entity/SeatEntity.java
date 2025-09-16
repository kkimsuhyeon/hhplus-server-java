package kr.hhplus.be.server.domain.concert.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import org.hibernate.annotations.Comment;

@Entity
public class SeatEntity {

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
    private Long price;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private ConcertScheduleEntity schedule;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
