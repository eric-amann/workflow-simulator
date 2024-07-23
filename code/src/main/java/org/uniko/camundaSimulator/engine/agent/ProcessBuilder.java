package org.uniko.camundaSimulator.engine.agent;

import lombok.Getter;
import org.uniko.camundaSimulator.engine.agent.schedule.ProcessSchedule;
import org.uniko.camundaSimulator.engine.configuration.ProcessConfig;
import org.uniko.camundaSimulator.engine.context.Modifier;
import org.uniko.camundaSimulator.engine.simulation.SimulationScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class ProcessBuilder {

    private SimulationScheduler simulation;
    private final String bpmnFilePath;
    private String community = "DEFAULT_COMMUNITY";
    private String group = "DEFAULT_GROUP";
    private String role = "DEFAULT_ROLE";
    private ProcessSchedule schedule = ProcessSchedule.create();
    private int startingInstances;
    private List<Modifier> modifiers = new ArrayList<>();

    public ProcessBuilder(String bpmnFilePath) {
        this.bpmnFilePath = bpmnFilePath;
    }

    public ProcessBuilder(ProcessConfig config) {
        this.bpmnFilePath = config.getFilePath();
        this.startingInstances = config.getStartingInstances();
    }

    public ProcessBuilder simulation(SimulationScheduler simulation) {
        this.simulation = simulation;
        return this;
    }

    public ProcessBuilder community(String community) {
        this.community = community;
        return this;
    }

    public ProcessBuilder group(String group) {
        this.group = group;
        return this;
    }

    public ProcessBuilder role(String role) {
        this.role = role;
        return this;
    }

    public ProcessBuilder schedule(ProcessSchedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public ProcessBuilder startingInstances(Integer startingInstances) {
        this.startingInstances = startingInstances;
        return this;
    }

    public ProcessBuilder modifier(Modifier modifier) {
        if (modifier == null) return this;
        this.modifiers.add(modifier);
        return this;
    }

    public ProcessBuilder modifiers(Collection<Modifier> modifiers) {
        if (modifiers == null) return this;
        this.modifiers.addAll(modifiers);
        return this;
    }

    public Process build() {
        return new Process(this);
    }
}
