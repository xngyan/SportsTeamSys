package com.sports.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "activity_id"}, name = "uk_user_activity")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User user;

    @Column(name = "activity_id", nullable = false)
    private Integer activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "registrations"})
    private Activity activity;

    @Column
    @Builder.Default
    private Integer status = 1;  // 1-待审核, 2-已通过, 3-已驳回, 4-已取消

    @Column(name = "registration_at", nullable = false)
    private LocalDateTime registrationAt;

    @Column(name = "cancel_at")
    private LocalDateTime cancelAt;

    @PrePersist
    protected void onCreate() {
        this.registrationAt = LocalDateTime.now();
    }
}
