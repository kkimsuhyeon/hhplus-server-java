package kr.hhplus.be.server.domain.concert.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "concerts")
public class ConcertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "concert_id")
    @Comment("콘서드 아이디")
    private String id;

    @Column(name = "title", nullable = false)
    @Comment("콘서트 제목")
    private String title;

    @Column(name = "description", nullable = false)
    @Comment("콘서트 설명")
    private String decription;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("등록 일시")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 일시")
    private LocalDateTime updatedAt;
}
