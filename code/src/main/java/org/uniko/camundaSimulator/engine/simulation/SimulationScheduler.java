package org.uniko.camundaSimulator.engine.simulation;

import com.google.common.collect.Multimap;
import lombok.Getter;
import madkit.kernel.AbstractAgent;
import madkit.kernel.Scheduler;
import madkit.simulation.activator.GenericBehaviorActivator;
import org.uniko.camundaSimulator.engine.agent.Process;
import org.uniko.camundaSimulator.engine.agent.ProcessBuilder;
import org.uniko.camundaSimulator.engine.agent.Worker;
import org.uniko.camundaSimulator.engine.agent.WorkerBuilder;
import org.uniko.camundaSimulator.service.DataModificationService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimulationScheduler extends Scheduler {

    private final List<String> processDefinitionKeys;
    private final LocalDateTime endDate;
    private final Integer maxInstances;
    @Getter
    private final Duration incrementPerTick;
    private final Multimap<Integer, WorkerBuilder> workers;
    private final List<ProcessBuilder> processes;
    @Getter
    private final TaskCoordinator taskCoordinator;
    private GenericBehaviorActivator<AbstractAgent> workerActivator;
    private GenericBehaviorActivator<AbstractAgent> processActivator;

    public SimulationScheduler(LocalDateTime startDate, LocalDateTime endDate, Integer maxInstances, Duration incrementPerTick, Multimap<Integer, WorkerBuilder> workers, List<ProcessBuilder> processes, TaskCoordinator taskCoordinator) {
        super(startDate);
        this.endDate = endDate;
        this.maxInstances = maxInstances;
        this.incrementPerTick = incrementPerTick;
        this.workers = workers;
        this.processes = processes;
        this.processDefinitionKeys = new ArrayList<>();
        this.taskCoordinator = taskCoordinator;
    }

    @Override
    protected void activate() {
        createGroup("DEFAULT_COMMUNITY", "DEFAULT_GROUP");
        createGroup(SimulationModel.TRIGGER_COMMUNITY, SimulationModel.TRIGGER_GROUP);

        workers.forEach(this::launchWorkers);
        workerActivator = new GenericBehaviorActivator<>("DEFAULT_COMMUNITY", "DEFAULT_GROUP", "DEFAULT_ROLE", "live");
        addActivator(workerActivator);

        for (ProcessBuilder processBuilder : processes) {
            processBuilder.simulation(this);
            Process process = processBuilder.build();
            processDefinitionKeys.add(String.valueOf(process.getDefinitionKey()));
            launchAgent(process);
        }
        processActivator = new GenericBehaviorActivator<>(SimulationModel.TRIGGER_COMMUNITY, SimulationModel.TRIGGER_GROUP, SimulationModel.TRIGGER_BOT_ROLE, "live");
        addActivator(processActivator);

        taskCoordinator.setProcessDefinitionKeys(processDefinitionKeys);
        getSimulationTime().setEndDate(endDate);
        setSimulationState(SimulationState.RUNNING);
    }

    @Override
    public void doSimulationStep() {

        if (maxInstances != null && maxInstances <= taskCoordinator.getStartedInstances())
            setSimulationState(SimulationState.SHUTDOWN);
        taskCoordinator.update();
        processActivator.execute();
        workerActivator.execute();

        getLogger().info(getSimulationTime().getCurrentDate().toString() + " - " + Duration.between(getSimulationTime().getCurrentDate(), endDate).getSeconds() / 60 + " minutes remaining");
        // propagate the simulation
        getSimulationTime().incrementCurrentDate(this.incrementPerTick.getSeconds());
    }

    @Override
    protected void end() {
        DataModificationService.get().modifyData(processDefinitionKeys);
    }

    private void launchWorkers(Integer amount, WorkerBuilder worker) {
        for (int i = 1; i <= amount; i++) {
            worker.simulation(this);
            launchAgent(worker.build());
        }
    }
}
