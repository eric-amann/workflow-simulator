package org.uniko.camundaSimulator.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uniko.camundaSimulator.model.SimulatedCase;
import org.uniko.camundaSimulator.model.SimulatedUserTask;
import org.uniko.camundaSimulator.repository.SimulatedCaseRepository;
import org.uniko.camundaSimulator.repository.SimulatedUserTaskRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
public class SimulationDataService implements InitializingBean {

    private static SimulationDataService simulationDataServiceInstance;

    @Autowired
    private SimulatedCaseRepository caseRepository;

    @Autowired
    private SimulatedUserTaskRepository taskRepository;

    public static SimulationDataService get() {
        return simulationDataServiceInstance;
    }

    public SimulatedCase saveNewCase(long processDefinitionKey, long processInstanceId, LocalDateTime startTime) {
        SimulatedCase simulatedCase = new SimulatedCase(processDefinitionKey, processInstanceId, startTime);
        return caseRepository.save(simulatedCase);
    }

    public SimulatedUserTask saveNewTask(long processInstanceId, String flowNodeId, LocalDateTime timestamp, OffsetDateTime originalCompletionDate) {
        SimulatedCase simulatedCase = caseRepository.getSimulatedCaseByProcessInstanceId(processInstanceId);
        SimulatedUserTask task = new SimulatedUserTask(flowNodeId, timestamp, originalCompletionDate, simulatedCase);
        return taskRepository.save(task);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        simulationDataServiceInstance = this;
    }
}
