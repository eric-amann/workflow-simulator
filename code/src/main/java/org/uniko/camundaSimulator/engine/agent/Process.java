package org.uniko.camundaSimulator.engine.agent;

import lombok.Getter;
import lombok.Setter;
import madkit.kernel.AbstractAgent;
import org.uniko.camundaSimulator.engine.agent.schedule.ProcessSchedule;
import org.uniko.camundaSimulator.engine.context.Modifier;
import org.uniko.camundaSimulator.engine.simulation.SimulationScheduler;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Process extends AbstractAgent {

    private final String bpmnFilePath;
    private final String community;
    private final String group;
    private final String role;
    private final ProcessSchedule schedule;
    private final Integer startingInstances;
    private final List<Modifier> modifiers;
    private final long definitionKey;
    @Setter
    private SimulationScheduler simulation;
    private LocalDateTime nextTrigger;

    public Process(ProcessBuilder builder) {
        this.simulation = builder.getSimulation();
        this.bpmnFilePath = builder.getBpmnFilePath();
        this.community = builder.getCommunity();
        this.group = builder.getGroup();
        this.role = builder.getRole();
        this.schedule = builder.getSchedule();
        this.startingInstances = builder.getStartingInstances();
        this.modifiers = builder.getModifiers();

        this.definitionKey = simulation.getTaskCoordinator().deployProcess(this.bpmnFilePath);
    }

    @Override
    protected void activate() {
        requestRole(community, group, role);
        for (int i = 0; i < startingInstances; i++) {
            startInstance();
        }
        nextTrigger = schedule.nextInstanceTrigger(getSimulationTime().getCurrentDate());
        getLogger().info("Deployed process " + bpmnFilePath);
    }

    public void live() {
        if (nextTrigger.isBefore(getSimulationTime().getCurrentDate()) || nextTrigger.isEqual(getSimulationTime().getCurrentDate())) {
            startInstance();
            nextTrigger = schedule.nextInstanceTrigger(nextTrigger);
        }
    }

    private void startInstance() {
        simulation.getTaskCoordinator().startInstance(definitionKey, getSimulationTime().getCurrentDate());
        getLogger().info("Started new instance of process definition " + definitionKey);
    }
}
