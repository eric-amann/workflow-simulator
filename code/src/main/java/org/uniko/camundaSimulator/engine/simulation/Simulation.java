package org.uniko.camundaSimulator.engine.simulation;

import org.uniko.camundaSimulator.engine.agent.ProcessBuilder;
import org.uniko.camundaSimulator.engine.agent.SimulatedTask;
import org.uniko.camundaSimulator.engine.agent.WorkerBuilder;
import org.uniko.camundaSimulator.engine.variable.ProcessVariable;
import org.uniko.camundaSimulator.engine.context.Context;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

public interface Simulation {

    static Builder builder(String id) {
        return new SimulationBuilder(id);
    }

    String getId();

    void start();

    void end();

    void restart();

    interface Builder {

        Builder startTime(LocalDateTime startTime);

        Builder endTime(LocalDateTime endTime);

        Builder maxInstances(Integer maxInstances);

        Builder incrementPerTick(Duration incrementPerTick);

        Builder withWorkers(Integer numberOfWorkers, WorkerBuilder worker);

        Builder withProcess(ProcessBuilder process);

        Builder withTask(SimulatedTask task);

        Builder withTasks(Collection<SimulatedTask> tasks);

        Builder withVariable(ProcessVariable variable);

        Builder withVariables(Collection<ProcessVariable> variables);

        Simulation build();
    }
}
