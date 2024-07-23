package org.uniko.camundaSimulator.engine.simulation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import madkit.kernel.Madkit;
import org.uniko.camundaSimulator.engine.agent.ProcessBuilder;
import org.uniko.camundaSimulator.engine.agent.SimulatedTask;
import org.uniko.camundaSimulator.engine.agent.WorkerBuilder;
import org.uniko.camundaSimulator.engine.variable.ProcessVariable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Getter
public class SimulationBuilder implements Simulation.Builder {

    private final String id;
    private final Madkit madkit = new Madkit(Madkit.LevelOption.madkitLogLevel.toString(), "INFO");
    private final TaskCoordinator taskCoordinator = new TaskCoordinator();
    private final List<ProcessBuilder> processes = new ArrayList<>();
    private final List<SimulatedTask> tasks = new ArrayList<>();
    private final List<ProcessVariable> variables = new ArrayList<>();
    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endTime = LocalDateTime.now().plusDays(30);
    private Integer maxInstances;
    private Duration incrementPerTick = Duration.ofMinutes(1);
    private Multimap<Integer, WorkerBuilder> workers = ArrayListMultimap.create();

    public SimulationBuilder(String id) {
        this.id = id;
    }

    @Override
    public Simulation.Builder startTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    @Override
    public Simulation.Builder endTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    @Override
    public Simulation.Builder maxInstances(Integer maxInstances) {
        this.maxInstances = maxInstances;
        return this;
    }

    @Override
    public Simulation.Builder incrementPerTick(Duration incrementPerTick) {
        this.incrementPerTick = incrementPerTick;
        return this;
    }

    @Override
    public Simulation.Builder withWorkers(Integer numberOfWorkers, WorkerBuilder worker) {
        if (worker == null || numberOfWorkers == null) return this;
        this.workers.put(numberOfWorkers, worker);
        return this;
    }

    @Override
    public Simulation.Builder withProcess(ProcessBuilder process) {
        if (process == null) return this;
        this.processes.add(process);
        return this;
    }

    @Override
    public Simulation.Builder withTask(SimulatedTask task) {
        if (task == null) return this;
        this.tasks.add(task);
        return this;
    }

    @Override
    public Simulation.Builder withTasks(Collection<SimulatedTask> tasks) {
        if (tasks == null) return this;
        this.tasks.addAll(tasks);
        return this;
    }

    @Override
    public Simulation.Builder withVariable(ProcessVariable variable) {
        if (variable == null) return this;
        this.variables.add(variable);
        return this;
    }

    @Override
    public Simulation.Builder withVariables(Collection<ProcessVariable> variables) {
        if (variables == null) return this;
        this.variables.addAll(variables);
        return this;
    }

    @Override
    public Simulation build() {
        taskCoordinator.setTaskConfigs(tasks);
        taskCoordinator.setProcessVariableConfigs(variables);
        return new SimulationImpl(this);
    }
}
