package org.uniko.camundaSimulator.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SimulatedUserTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String flowNodeId;
    private LocalDateTime timestamp;
    private OffsetDateTime originalCompletionTimestamp;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "related_case_id", nullable = false)
    @JsonBackReference
    private SimulatedCase simulatedCase;
    private boolean processed;

    public SimulatedUserTask(String flowNodeId, LocalDateTime timestamp, OffsetDateTime originalCompletionTimestamp, SimulatedCase simulatedCase) {
        this.flowNodeId = flowNodeId;
        this.timestamp = timestamp;
        this.simulatedCase = simulatedCase;
        this.originalCompletionTimestamp = originalCompletionTimestamp;
        this.processed = false;
    }
}
