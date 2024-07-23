package org.uniko.camundaSimulator.service;

import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ZeebeService implements InitializingBean {

    private static ZeebeService ZeebeServiceInstance;

    private final SimulationDataService simulationDataService;

    private CamundaConfigurationService config;

    @Autowired
    public ZeebeService(CamundaConfigurationService config, SimulationDataService simulationDataService) {
        this.config = config;
        this.simulationDataService = simulationDataService;
    }

    public static ZeebeService get() {
        return ZeebeServiceInstance;
    }

    public ProcessInstanceEvent startInstance(long definitionKey, LocalDateTime timestamp, Map<String, Object> variables) {
        ProcessInstanceEvent event = config.getZeebeClient()
                .newCreateInstanceCommand()
                .processDefinitionKey(definitionKey)
                .variables(variables)
                .send()
                .join();

        simulationDataService.saveNewCase(event.getProcessDefinitionKey(), event.getProcessInstanceKey(), timestamp);
        return event;
    }

    //public DeploymentEvent deployResource(String resourceFilePath) {
    public DeploymentEvent deployResource(BpmnModelInstance modelInstance, String resourceName) {

        return config.getZeebeClient()
                .newDeployResourceCommand()
                //.addResourceFile(resourceFilePath)
                .addProcessModel(modelInstance, resourceName)
                .send()
                .join();
    }

    @Override
    public void afterPropertiesSet() {
        ZeebeServiceInstance = this;
    }
}
