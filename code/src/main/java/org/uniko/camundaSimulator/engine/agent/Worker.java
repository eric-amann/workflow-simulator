package org.uniko.camundaSimulator.engine.agent;

import lombok.Getter;
import lombok.Setter;
import madkit.kernel.AbstractAgent;
import org.uniko.camundaSimulator.engine.agent.schedule.WorkSchedule;
import org.uniko.camundaSimulator.engine.context.ContextApplicationTarget;
import org.uniko.camundaSimulator.engine.context.Modifier;
import org.uniko.camundaSimulator.engine.simulation.SimulationScheduler;

import java.time.Duration;
import java.util.List;


public class Worker extends AbstractAgent {
    private final String community;
    private final String group;
    private final String role;
    private final double baselinePerformance;
    private final WorkSchedule schedule;
    @Getter
    private final List<String> allowedTaskIds;
    private final List<Modifier> modifiers;
    @Setter
    private SimulationScheduler simulation;
    private WorkerStatus status;
    private String executedTaskId;
    private String executedTaskDefinitionId;
    private Duration processingTime;
    private Boolean completeTask;

    public Worker(WorkerBuilder builder) {
        this.simulation = builder.getSimulation();
        this.community = builder.getCommunity();
        this.group = builder.getGroup();
        this.role = builder.getRole();
        this.baselinePerformance = builder.getBaselinePerformance();
        this.schedule = builder.getSchedule();
        this.allowedTaskIds = builder.getAllowedTaskDefinitionIds();
        this.modifiers = builder.getModifiers();
        this.completeTask = builder.getCompleteTask();
    }

    @Override
    protected void activate() {
        requestRole(community, group, role);
        status = WorkerStatus.IDLE;
    }

    @SuppressWarnings("unused")
    public void live() {

        setName(modifiers.stream()
                .filter(mod -> mod.getTarget().equals(ContextApplicationTarget.NAME))
                .findFirst()
                .map(mod -> mod.apply(getSimulationTime().getCurrentDate(), getName()))
                .orElse(getName()));

        // execute if on duty
        if (schedule.isActive(getSimulationTime().getCurrentDate())) {
            if (executedTaskDefinitionId == null) {
                requestTask();
                getLogger().info("I'm requesting a task.");
            } else {
                processTask();
            }

            // execute if off duty
        } else {
            if (!this.status.equals(WorkerStatus.OFF_DUTY)) this.status = WorkerStatus.OFF_DUTY;
            getLogger().info("I'm inactive.");
        }
    }

    public void requestTask() {
        simulation.getTaskCoordinator().requestTask(this);
    }

    public void processTask() {
        double performance = baselinePerformance;
        List<Modifier> mods = modifiers.stream()
                .filter(mod -> mod.getTarget().equals(ContextApplicationTarget.PERFORMANCE))
                .toList();

        for (Modifier mod : mods) {
            performance = mod.apply(getSimulationTime().getCurrentDate(), performance);
        }

        processingTime = processingTime.minus(Duration.ofSeconds(Math.round(simulation.getIncrementPerTick().getSeconds() * performance)));
        if (processingTime.isZero() || processingTime.isNegative()) completeTask();
        getLogger().info("I'm working.");
    }

    public void completeTask() {

        simulation.getTaskCoordinator().completeTask(executedTaskId, executedTaskDefinitionId, getSimulationTime().getCurrentDate());

        getLogger().info("I completed task " + executedTaskDefinitionId);
        status = WorkerStatus.IDLE;
        executedTaskDefinitionId = null;
    }

    public void assignTask(String taskId, String taskDefinitionId, Duration taskDuration) {
        executedTaskId = taskId;
        executedTaskDefinitionId = taskDefinitionId;
        processingTime = taskDuration;
        status = WorkerStatus.EXECUTING_TASK;
        getLogger().info("I claimed task " + executedTaskDefinitionId);
    }
}
