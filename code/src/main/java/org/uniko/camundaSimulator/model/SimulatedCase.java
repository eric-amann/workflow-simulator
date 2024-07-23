package org.uniko.camundaSimulator.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SimulatedCase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long processDefinitionKey;
    private long processInstanceId;
    @OneToMany(mappedBy = "simulatedCase", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<SimulatedUserTask> userTasks;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public SimulatedCase(long processDefinitionKey, long processInstanceId, LocalDateTime startDate) {
        this.processDefinitionKey = processDefinitionKey;
        this.processInstanceId = processInstanceId;
        this.startDate = startDate;
        this.userTasks = new HashSet<>();
    }

}
