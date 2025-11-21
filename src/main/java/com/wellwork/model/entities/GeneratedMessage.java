package com.wellwork.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "generated_messages")
public class GeneratedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_generated_messages")
    @SequenceGenerator(name = "seq_generated_messages", sequenceName = "SEQ_GENERATED_MESSAGES", allocationSize = 1)
    @Getter @Setter
    private Long id;

    @OneToOne
    @JoinColumn(name = "checkin_id", nullable = false)
    @Getter @Setter
    private CheckIn checkIn;

    @Lob
    @Column(columnDefinition = "CLOB")
    @Getter @Setter
    private String message;

    @Getter @Setter
    private Double confidence;

    @Getter @Setter
    private Instant generatedAt = Instant.now();

    public GeneratedMessage() {}

    public GeneratedMessage(Long id, CheckIn checkIn, String message, Double confidence, Instant generatedAt) {
        this.id = id;
        this.checkIn = checkIn;
        this.message = message;
        this.confidence = confidence;
        this.generatedAt = generatedAt;
    }
}
