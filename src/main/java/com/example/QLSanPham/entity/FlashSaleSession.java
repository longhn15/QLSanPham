package com.example.QLSanPham.entity;

import com.example.QLSanPham.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "FlashSaleSessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // VD: Sale 12.12

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private FlashSaleStatus status = FlashSaleStatus.PENDING; // PENDING, ACTIVE, FINISHED

    @OneToMany(mappedBy = "flashSaleSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FlashSaleItem> flashSaleItems;
}

enum FlashSaleStatus {
    PENDING, ACTIVE, FINISHED
}