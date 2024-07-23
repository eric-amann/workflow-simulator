package org.uniko.camundaSimulator.engine.simulation;


import com.google.common.collect.Multimap;
import lombok.Getter;
import madkit.action.KernelAction;
import madkit.kernel.Madkit;
import org.uniko.camundaSimulator.engine.agent.Process;
import org.uniko.camundaSimulator.engine.agent.ProcessBuilder;
import org.uniko.camundaSimulator.engine.agent.Worker;
import org.uniko.camundaSimulator.engine.agent.WorkerBuilder;
import org.uniko.camundaSimulator.engine.context.Context;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SimulationImpl implements Simulation {

    @Getter
    private final String id;
    private final Madkit madkit;
    private final TaskCoordinator taskCoordinator;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer maxInstances;
    private final Duration incrementPerTick;
    private final Multimap<Integer, WorkerBuilder> workers;
    private final List<ProcessBuilder> processes;

    public SimulationImpl(SimulationBuilder builder) {
        this.id = builder.getId();
        this.madkit = builder.getMadkit();
        this.taskCoordinator = builder.getTaskCoordinator();
        this.startDate = builder.getStartTime();
        this.endDate = builder.getEndTime();
        this.maxInstances = builder.getMaxInstances();
        this.incrementPerTick = builder.getIncrementPerTick();
        this.workers = builder.getWorkers();
        this.processes = builder.getProcesses();
    }

    @Override
    public void start() {
        SimulationScheduler scheduler = new SimulationScheduler(this.startDate, this.endDate, this.maxInstances, this.incrementPerTick, this.workers, this.processes, this.taskCoordinator);
        madkit.doAction(KernelAction.LAUNCH_AGENT, scheduler);
    }

    @Override
    public void end() {
        madkit.doAction(KernelAction.EXIT);
    }

    @Override
    public void restart() {
        madkit.doAction(KernelAction.RESTART);
    }
}
