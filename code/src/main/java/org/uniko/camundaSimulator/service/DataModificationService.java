package org.uniko.camundaSimulator.service;

import io.camunda.operate.model.FlowNodeInstance;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uniko.camundaSimulator.dto.elasticsearch.document.OperateEvent;
import org.uniko.camundaSimulator.dto.elasticsearch.document.OperateFlowNodeInstance;
import org.uniko.camundaSimulator.dto.elasticsearch.document.OperateListView;
import org.uniko.camundaSimulator.dto.elasticsearch.document.TasklistTask;
import org.uniko.camundaSimulator.model.SimulatedCase;
import org.uniko.camundaSimulator.model.SimulatedUserTask;
import org.uniko.camundaSimulator.repository.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

@Service
public class DataModificationService implements InitializingBean {

    static final Logger LOGGER = Logger.getLogger(DataModificationService.class.getPackageName());
    private static DataModificationService dataModificationServiceInstance;
    private final DateTimeFormatter offsetDateTimeFormatter;
    @Autowired
    private OperateEventRepository eventRepository;
    @Autowired
    private OperateFlowNodeInstanceRepository flowNodeInstanceRepository;
    @Autowired
    private SimulatedCaseRepository caseRepository;
    @Autowired
    private OperateListViewRepository listViewRepository;
    @Autowired
    private TasklistTaskRepository tasklistRepository;
    @Autowired
    private OperateService operateService;

    public DataModificationService() {
        this.offsetDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXXX");
    }

    public static DataModificationService get() {
        return dataModificationServiceInstance;
    }

    public void modifyData(List<String> processDefinitionKeys) {
        for (String key : processDefinitionKeys) {
            Iterable<SimulatedCase> cases = caseRepository.findAllByProcessDefinitionKey(Long.parseLong(key));
            cases.forEach(this::modifyCase);
            LOGGER.info("Updated all process instance entries for process " + key);
        }
    }

    private void modifyCase(SimulatedCase simulatedCase) {

        LOGGER.info("Updating simulated case no. " + simulatedCase.getId());

        Set<FlowNodeInstance> flowNodeInstances = operateService.getAllFlowNodesByProcessInstanceKey(simulatedCase.getProcessInstanceId());
        Map<String, Set<String>> activitySequences = operateService.getProcessSequenceFlows(String.valueOf(simulatedCase.getProcessDefinitionKey()));
        if (flowNodeInstances.iterator().hasNext()) {
            OffsetDateTime instanceEndDate = recursiveEntryUpdate(
                    simulatedCase,
                    flowNodeInstances.stream().filter(instance -> "START_EVENT".equals(instance.getType())).findFirst().get(),
                    flowNodeInstances,
                    activitySequences,
                    null);

            //Update operate list view
            OperateListView listViewEntry = listViewRepository.getListViewByProcessDefinitionKeyAndProcessInstanceKey(
                    simulatedCase.getProcessDefinitionKey(),
                    simulatedCase.getProcessInstanceId());

            listViewEntry.setStartDate(formatDate(simulatedCase.getStartDate()));
            if (listViewEntry.getEndDate() != null) {
                listViewEntry.setEndDate(formatDate(instanceEndDate.toLocalDateTime()));
            }
            saveListView(listViewEntry);
            LOGGER.info("Completed updating instance " + simulatedCase.getProcessInstanceId());
        }
    }

    private void entryUpdate(SimulatedCase simulatedCase, FlowNodeInstance instance, Set<FlowNodeInstance> instances, Map<String, Set<String>> activitySequences, String previousEndDate) {

        //Get flow node instance entry
        OperateFlowNodeInstance instanceEntry = flowNodeInstanceRepository.getFlowNodeInstanceByProcessInstanceKeyAndFlowNodeId(
                        simulatedCase.getProcessInstanceId(),
                        instance.getFlowNodeId())
                .orElseThrow();
        //Get operate event entry
        OperateEvent eventEntry = eventRepository.getOperateEventByProcessInstanceKeyAndFlowNodeId(
                        simulatedCase.getProcessInstanceId(),
                        instance.getFlowNodeId())
                .orElseThrow();
    }

    private OffsetDateTime recursiveEntryUpdate(SimulatedCase simulatedCase, FlowNodeInstance instance, Set<FlowNodeInstance> instances, Map<String, Set<String>> activitySequences, String previousEndDate) {

        OperateFlowNodeInstance instanceEntry;
        OperateEvent eventEntry;
        String endDate = previousEndDate;
        LOGGER.info("Working on instance " + instance.getFlowNodeId() + " of process instance " + instance.getProcessInstanceKey());

        //Get flow node instance entry
        Optional<OperateFlowNodeInstance> instanceEntryOptional = flowNodeInstanceRepository.getFlowNodeInstanceByProcessInstanceKeyAndFlowNodeId(
                simulatedCase.getProcessInstanceId(),
                instance.getFlowNodeId());
        //Get operate event entry
        Optional<OperateEvent> eventEntryOptional = eventRepository.getOperateEventByProcessInstanceKeyAndFlowNodeId(
                simulatedCase.getProcessInstanceId(),
                instance.getFlowNodeId());

        if (instanceEntryOptional.isPresent() && eventEntryOptional.isPresent()) {
            instanceEntry = instanceEntryOptional.get();
            eventEntry = eventEntryOptional.get();
        } else {
            return null;
        }


        //Get the relevant dates for each flow node type
        switch (instance.getType()) {
            case "START_EVENT":
                endDate = formatDate(simulatedCase.getStartDate());
                instanceEntry.setStartDate(endDate);
                break;
            case "USER_TASK":
                Optional<SimulatedUserTask> simulatedUserTask = simulatedCase.getUserTasks().stream()
                        .filter(task -> instance.getFlowNodeId().equals(task.getFlowNodeId()) && !task.isProcessed())
                        .min(Comparator.comparing(
                                task -> task.getOriginalCompletionTimestamp().until(OffsetDateTime.parse(instanceEntry.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")), ChronoUnit.NANOS)
                        ));

                //Get tasklist task entry
                Optional<TasklistTask> taskEntry = tasklistRepository.getTasklistTaskByProcessInstanceIdAndFlowNodeInstanceId(instance.getProcessInstanceKey(), instanceEntry.getId());
                if (taskEntry.isPresent()) {
                    taskEntry.get().setCreationTime(previousEndDate);

                    //Set end date if user task was completed
                    if (simulatedUserTask.isPresent()) {
                        endDate = formatDate(simulatedUserTask.get().getTimestamp());
                        taskEntry.get().setCompletionTime(endDate);
                    }

                    saveTask(taskEntry.get());
                }
            default:
                instanceEntry.setStartDate(previousEndDate);

        }
        if (!"ACTIVE".equals(instanceEntry.getState())) instanceEntry.setEndDate(endDate);
        eventEntry.setDateTime(endDate);

        //Save DB entries
        saveFlowNodeInstance(instanceEntry);
        saveEvent(eventEntry);

        LOGGER.info("Updated activity " + instance.getFlowNodeId() + " of process instance " + instance.getProcessInstanceKey());

        //Search for all following flow node instances and update them
        if (!"END_EVENT".equals(instance.getType()) && !"ACTIVE".equals(instanceEntry.getState())) {
            OffsetDateTime processEndDate = null;
            for (String nextActivityId : activitySequences.get(instance.getFlowNodeId())) {
                Optional<FlowNodeInstance> optionalInstance = instances.stream()
                        .filter(i -> nextActivityId.equals(i.getFlowNodeId()) && i.getStartDate().equals(instance.getEndDate()))
                        .findFirst();
                if (optionalInstance.isPresent()) {
                    OffsetDateTime result = recursiveEntryUpdate(simulatedCase, optionalInstance.get(), instances, activitySequences, endDate);
                    if (result != null) {
                        if (processEndDate == null || processEndDate.isBefore(result)) processEndDate = result;
                    }
                }
            }
            return processEndDate;
        } else {
            return OffsetDateTime.parse(previousEndDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"));
        }
    }

    private OperateFlowNodeInstance saveFlowNodeInstance(OperateFlowNodeInstance instance) {
        //instance.setModified(true);
        return flowNodeInstanceRepository.save(instance);
    }

    private OperateEvent saveEvent(OperateEvent event) {
        //event.setModified(true);
        return eventRepository.save(event);
    }

    private OperateListView saveListView(OperateListView listView) {
        //listView.setModified(true);
        return listViewRepository.save(listView);
    }

    private TasklistTask saveTask(TasklistTask task) {
        return tasklistRepository.save(task);
    }

    private String formatDate(LocalDateTime dateTime) {
        return OffsetDateTime.of(dateTime, ZoneOffset.MIN).format(offsetDateTimeFormatter);
    }

    @Override
    public void afterPropertiesSet() {
        dataModificationServiceInstance = this;
    }
}
