package org.uniko.camundaSimulator.engine.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uniko.camundaSimulator.engine.agent.ProcessBuilder;
import org.uniko.camundaSimulator.engine.agent.SimulatedTask;
import org.uniko.camundaSimulator.engine.agent.WorkerBuilder;
import org.uniko.camundaSimulator.engine.agent.schedule.ProcessSchedule;
import org.uniko.camundaSimulator.engine.agent.schedule.WorkSchedule;
import org.uniko.camundaSimulator.engine.context.Context;
import org.uniko.camundaSimulator.engine.context.ContextBuilder;
import org.uniko.camundaSimulator.engine.context.Modifier;
import org.uniko.camundaSimulator.engine.variable.ProcessVariableBuilder;
import org.uniko.camundaSimulator.engine.simulation.Simulation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimulationConfig {

    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxInstances;
    private Duration incrementPerTick;
    private List<WorkerConfig> workerConfigs = new ArrayList<>();
    private List<ProcessConfig> processConfigs = new ArrayList<>();
    private List<TaskConfig> taskConfigs = new ArrayList<>();
    private VariablesListConfig variableConfigs;
    private List<ContextConfig> contextConfigs = new ArrayList<>();
    private List<ModifierConfig> modifierConfigs = new ArrayList<>();
    private List<ScheduleConfig> scheduleConfigs = new ArrayList<>();

    public Simulation createSimulation() {

        Simulation.Builder builder = Simulation.builder(id)
                .startTime(startTime)
                .endTime(endTime)
                .maxInstances(maxInstances)
                .incrementPerTick(incrementPerTick);

        List<Context> contexts = createContexts();

        addWorkers(builder, contexts);
        addProcesses(builder, contexts);
        addTasks(builder, contexts);
        addVariables(builder, contexts);

        return builder.build();
    }

    private List<Modifier> createModifiers(String configElemId, List<Context> contexts) {

        List<Modifier> modifiers = new ArrayList<>();
        List<ModifierConfig> configs = this.modifierConfigs.stream()
                .filter(mod -> mod.getTargetId().equals(configElemId))
                .toList();

        for (ModifierConfig config : configs) {
            Optional<Context> context = contexts.stream()
                    .filter(c -> c.getId().equals(config.getContextId()))
                    .findFirst();

            if(context.isEmpty()) continue;
            modifiers.add(new Modifier(
                    context.get(),
                    config.getColumn(),
                    config.getContextApplicationType(),
                    config.getContextApplicationTarget()
            ));
        }
        return modifiers;
    }


    private void addWorkers(Simulation.Builder simulationBuilder,  List<Context> contexts) {

        for (WorkerConfig config : workerConfigs) {
            // Create schedule
            WorkSchedule schedule = scheduleConfigs.stream()
                    .filter(s -> s.getId().equalsIgnoreCase(config.getScheduleId()))
                    .findFirst()
                    .map(ScheduleConfig::buildWorkSchedule)
                    .orElse(WorkSchedule.create());

            WorkerBuilder workerBuilder = new WorkerBuilder(config)
                    .schedule(schedule)
                    .modifiers(createModifiers(config.getId(), contexts))
                    .completeTask(config.getCompleteTask());

            simulationBuilder.withWorkers(config.getAmount(), workerBuilder);
        }
    }

    private void addProcesses(Simulation.Builder simulationBuilder, List<Context> contexts) {
        for (ProcessConfig config : processConfigs) {

            ProcessSchedule schedule = scheduleConfigs.stream()
                    .filter(s -> s.getId().equalsIgnoreCase(config.getScheduleId()))
                    .findFirst()
                    .map(s -> s.buildProcessSchedule(config.getInterval(), createModifiers(config.getId(), contexts)))
                    .orElse(ProcessSchedule.create());

            ProcessBuilder processBuilder = new ProcessBuilder(config)
                    .schedule(schedule)
                    .modifiers(createModifiers(config.getId(), contexts));

            simulationBuilder.withProcess(processBuilder);
        }
    }

    private void addTasks(Simulation.Builder simulationBuilder, List<Context> contexts) {
        for (TaskConfig config : taskConfigs) {
            for (String id : config.getId()) {
                SimulatedTask task = new SimulatedTask(
                        id,
                        config.getProcessingTime(),
                        createModifiers(id, contexts)
                );
                simulationBuilder.withTask(task);
            }
        }
    }

    private void addVariables(Simulation.Builder simulationBuilder, List<Context> contexts) {
        for (ProcessVariableConfig config : variableConfigs.getVariables()) {
            ProcessVariableBuilder variableBuilder = new ProcessVariableBuilder(config);
            variableBuilder.modifiers(createModifiers(config.getName(),contexts));
            simulationBuilder.withVariable(variableBuilder.build());
        }
    }

    private List<Context> createContexts() {
        List<Context> contexts = new ArrayList<>();
        for (ContextConfig config : this.contextConfigs) {
            contexts.add(new ContextBuilder(config).build());
        }
        return contexts;
    }
}
