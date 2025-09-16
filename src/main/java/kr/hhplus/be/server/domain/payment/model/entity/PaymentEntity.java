package kr.hhplus.be.server.domain.payment.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;
import org.hibernate.annotations.Comment;

@Entity
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", nullable = false)
    @Comment("결제 아이디")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("결제 상태")
    private PaymentStatus status;

    @Column(name = "amount", nullable = false)
    @Comment("가격")
    private Long amount;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;
}
