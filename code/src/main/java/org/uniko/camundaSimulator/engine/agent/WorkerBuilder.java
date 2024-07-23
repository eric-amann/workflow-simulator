package org.uniko.camundaSimulator.engine.agent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.uniko.camundaSimulator.engine.agent.schedule.WorkSchedule;
import org.uniko.camundaSimulator.engine.configuration.WorkerConfig;
import org.uniko.camundaSimulator.engine.context.Modifier;
import org.uniko.camundaSimulator.engine.simulation.SimulationScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
public class WorkerBuilder {
    private SimulationScheduler simulation;
    private String community = "DEFAULT_COMMUNITY";
    private String group = "DEFAULT_GROUP";
    private String role = "DEFAULT_ROLE";
    private Double baselinePerformance = 1.0;
    private WorkSchedule schedule = WorkSchedule.create();
    private List<String> allowedTaskDefinitionIds = new ArrayList<>();
    private List<Modifier> modifiers = new ArrayList<>();
    private Boolean completeTask;

    public WorkerBuilder(WorkerConfig config) {
        //this.community = config.getCommunity();
        //this.group = config.getGroup();
        //this.role = config.getRole();
        this.baselinePerformance = config.getBaselinePerformance();
        this.allowedTaskDefinitionIds = config.getAllowedTaskIds();
    }

    public WorkerBuilder simulation(SimulationScheduler simulation) {
        this.simulation = simulation;
        return this;
    }

    public WorkerBuilder community(String community) {
        this.community = community;
        return this;
    }

    public WorkerBuilder group(String group) {
        this.group = group;
        return this;
    }

    public WorkerBuilder role(String role) {
        this.role = role;
        return this;
    }

    public WorkerBuilder baselinePerformance(Double baselinePerformance) {
        this.baselinePerformance = baselinePerformance;
        return this;
    }

    public WorkerBuilder schedule(WorkSchedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public WorkerBuilder addAllowedTaskId(String taskDefinitionId) {
        if (taskDefinitionId == null) return this;
        this.allowedTaskDefinitionIds.add(taskDefinitionId);
        return this;
    }

    public WorkerBuilder addAllowedTaskIds(Collection<String> taskDefinitionIds) {
        if (taskDefinitionIds == null) return this;
        this.allowedTaskDefinitionIds.addAll(taskDefinitionIds);
        return this;
    }

    public WorkerBuilder modifier(Modifier modifier) {
        if (modifier == null) return this;
        this.modifiers.add(modifier);
        return this;
    }

    public WorkerBuilder modifiers(Collection<Modifier> modifiers) {
        if (modifiers == null) return this;
        this.modifiers.addAll(modifiers);
        return this;
    }

    public WorkerBuilder completeTask(Boolean completeTask) {
        this.completeTask = completeTask;
        return this;
    }

    public Worker build() {
        return new Worker(this);
    }
}
