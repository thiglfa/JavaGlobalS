package com.wellwork.model.entities;

import com.wellwork.model.enums.EnergyLevel;
import com.wellwork.model.enums.Mood;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "checkins")
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_checkins")
    @SequenceGenerator(name = "seq_checkins", sequenceName = "SEQ_CHECKINS", allocationSize = 1)
    @Getter @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Getter @Setter
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Getter @Setter
    private Mood mood;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Getter @Setter
    private EnergyLevel energyLevel;

    // Oracle não tem TEXT → usar CLOB
    @Lob
    @Getter @Setter
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Getter @Setter
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "checkIn", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private GeneratedMessage generatedMessage;

    public CheckIn() {}

    public CheckIn(Long id, User user, Mood mood, EnergyLevel energyLevel, String notes,
                   LocalDateTime createdAt, GeneratedMessage generatedMessage) {
        this.id = id;
        this.user = user;
        this.mood = mood;
        this.energyLevel = energyLevel;
        this.notes = notes;
        this.createdAt = createdAt;
        this.generatedMessage = generatedMessage;
    }

    public void updateMood(Mood mood) {
        this.mood = mood;
    }

    public void updateEnergy(EnergyLevel energy) {
        this.energyLevel = energy;
    }

    public void updateNotes(String notes) {
        this.notes = notes;
    }
}
