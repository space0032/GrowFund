package com.growfund.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Data
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // Firebase UID

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime submittedAt = LocalDateTime.now();

    private String appVersion;
    private String deviceModel;
}
