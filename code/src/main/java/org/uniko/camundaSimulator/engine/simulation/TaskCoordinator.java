package org.uniko.camundaSimulator.engine.simulation;

import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.model.bpmn.instance.Definitions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.uniko.camundaSimulator.dto.*;
import org.uniko.camundaSimulator.engine.agent.SimulatedTask;
import org.uniko.camundaSimulator.engine.agent.Worker;
import org.uniko.camundaSimulator.engine.variable.ProcessVariable;
import org.uniko.camundaSimulator.model.SimulatedUserTask;
import org.uniko.camundaSimulator.service.SimulationDataService;
import org.uniko.camundaSimulator.service.TaskService;
import org.uniko.camundaSimulator.service.ZeebeService;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class TaskCoordinator {

    private final TaskService taskService;
    private final ZeebeService zeebeService;
    private final SimulationDataService simulationDataService;
    private List<Task> openTasks;
    private Map<String, Task> claimedTasks;
    private List<CompletedTask> completedTasks;
    @Setter
    @Getter
    private List<String> processDefinitionKeys;
    @Setter
    private List<SimulatedTask> taskConfigs;
    @Setter
    private List<ProcessVariable> processVariableConfigs;
    @Getter
    private int startedInstances;

    public TaskCoordinator() {
        this.claimedTasks = new HashMap<>();
        this.completedTasks = new ArrayList<>();
        this.taskService = TaskService.get();
        this.zeebeService = ZeebeService.get();
        this.simulationDataService = SimulationDataService.get();
    }

    public void update() {
        executeTaskInteractions();

        TaskSearchRequest request = new TaskSearchRequest();
        request.setProcessDefinitionKey(processDefinitionKeys.getFirst());
        request.setTaskState(TaskState.CREATED);
        request.setAssigned(false);
        request.setPageSize(1000);

        ResponseEntity<List<Task>> response = taskService.searchTasks(request);
        assert response != null;
        if (response.getStatusCode().is2xxSuccessful()) {
            openTasks = response.getBody();
        }
    }

    public void requestTask(Worker worker) {

        Optional<Task> optionalTask = openTasks.stream()
                .filter(task -> processDefinitionKeys.contains(task.getProcessDefinitionKey()))
                .filter(task -> {
                    if (!worker.getAllowedTaskIds().isEmpty()) return worker.getAllowedTaskIds().contains(task.getTaskDefinitionId());
                    return true;
                })
                .findFirst();

        if (optionalTask.isPresent()) {
            openTasks.remove(optionalTask.get());
            claimedTasks.put(worker.getName(), optionalTask.get());

            Duration taskDuration = taskConfigs.stream()
                    .filter(task -> task.getTaskId().equals(optionalTask.get().getId()))
                    .findFirst()
                    .map(task -> task.getProcessingTime(worker.getSimulationTime().getCurrentDate()))
                    .orElseGet(() -> taskConfigs.stream()
                            .filter(task -> task.getTaskId().equals("SIM_DEFAULT"))
                            .findFirst()
                            .map(task -> task.getProcessingTime(worker.getSimulationTime().getCurrentDate()))
                            .orElse(Duration.ofMinutes(15))
                    );
            worker.assignTask(optionalTask.get().getId(), optionalTask.get().getTaskDefinitionId(), taskDuration);
        }
    }

    public void completeTask(String taskId, String taskDefinitionId, LocalDateTime timestamp) {
        completedTasks.add(new CompletedTask(taskId, taskDefinitionId, timestamp));
    }

    private void executeTaskInteractions() {

        List<Flux<TaskResponse>> publishers = new ArrayList<>();
        List<Flux<SimulatedUserTask>> completedPublishers = new ArrayList<>();
        for (Map.Entry<String, Task> claimedTask : claimedTasks.entrySet()) {
            publishers.add(taskService.assignTaskNonBlocking(claimedTask.getValue().getId(), claimedTask.getKey()));
        }

        for (CompletedTask completedTask : completedTasks) {

            // Get variables
            TaskCompleteRequest request = new TaskCompleteRequest(new ArrayList<>());
            processVariableConfigs.stream()
                    .filter(variable -> variable.getTaskDefinitionIds().contains(completedTask.taskDefinitionId))
                    .forEach(variable -> request.getVariables().add(new VariableInput(variable.getName(), variable.getValue(completedTask.getTimestamp()))));

            // Send request and save completed task in db
            completedPublishers.add(taskService.completeTaskRequestNonBlocking(completedTask.getTaskId(), request)
                    .map(taskResponse -> simulationDataService.saveNewTask(
                            Long.parseLong(taskResponse.getProcessInstanceKey()),
                            taskResponse.getTaskDefinitionId(),
                            completedTask.getTimestamp(),
                            taskResponse.getCompletionDate())
                    ));
        }
        Flux.merge(publishers).blockLast();
        Flux.merge(completedPublishers).blockLast();

        this.completedTasks = new ArrayList<>();
        this.claimedTasks = new HashMap<>();
    }

    public void startInstance(long definitionKey, LocalDateTime timestamp) {
        Map<String, Object> variables = new HashMap<>();
        zeebeService.startInstance(definitionKey, timestamp, variables);
        startedInstances++;
    }

    public long deployProcess(String resourceFilePath) {

        File bpmnFile = new File(resourceFilePath);
        BpmnModelInstance model = Bpmn.readModelFromFile(bpmnFile);
        Collection<Definitions> sequenceFlows = model.getModelElementsByType(Definitions.class);
        Optional<Definitions> definition = sequenceFlows.stream().findFirst();

        definition.ifPresent(definitions -> definitions.setName(UUID.randomUUID().toString()));

        return zeebeService.deployResource(model, bpmnFile.getName()).getProcesses().getFirst().getProcessDefinitionKey();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CompletedTask {

        private String taskId;
        private String taskDefinitionId;
        private LocalDateTime timestamp;
    }
}
