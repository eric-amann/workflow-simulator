package org.uniko.camundaSimulator.service;

import io.camunda.operate.model.FlowNodeInstance;
import io.camunda.operate.search.FlowNodeInstanceFilter;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.model.bpmn.instance.SequenceFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.uniko.camundaSimulator.dto.FlowNodeInstanceResponse;
import org.uniko.camundaSimulator.dto.FlowNodeInstanceSearchRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class OperateService {

    private static final String SEARCH_FLOW_NODE_INSTANCES_PATH = "/v1/flownode-instances/search";
    private static final String SEARCH_VARIABLES_PATH = "/v1/variables/search";
    private static final String GET_BPMN_XML_PATH = "/v1/process-definitions/{key}/xml";
    @Autowired
    private CamundaConfigurationService config;

    public Set<FlowNodeInstance> getAllFlowNodesByProcessInstanceKey(long processInstanceKey) {
        Set<FlowNodeInstance> flowNodeInstances = new HashSet<>();
        int pageSize = 100;
        int total = 0;
        FlowNodeInstanceSearchRequest request = new FlowNodeInstanceSearchRequest(
                FlowNodeInstanceFilter.builder().processInstanceKey(processInstanceKey).build(),
                pageSize,
                new long[]{0}
        );
        do {
            ResponseEntity<FlowNodeInstanceResponse> response = searchFlowNodeRequest(request);
            if (response.getStatusCode().is2xxSuccessful()) {
                flowNodeInstances.addAll(Objects.requireNonNull(response.getBody()).getItems());
                request.setSearchAfter(response.getBody().getSortValues());
                if (total == 0) total = response.getBody().getTotal();
            }
        } while (flowNodeInstances.size() < total);
        return flowNodeInstances;
    }

    private ResponseEntity<FlowNodeInstanceResponse> searchFlowNodeRequest(FlowNodeInstanceSearchRequest request) {
        try {
            URI requestUri = new URI(config.getOperateAddress() + SEARCH_FLOW_NODE_INSTANCES_PATH);
            ResponseEntity<FlowNodeInstanceResponse> response = config.getRestTemplate().exchange(
                    requestUri,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<>() {
                    }
            );
            return response;

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Set<String>> getProcessSequenceFlows(String processDefinitionsKey) {
        try {
            URI requestUri = new URI(config.getOperateAddress() + GET_BPMN_XML_PATH.replace("{key}", processDefinitionsKey));
            ResponseEntity<Resource> response = config.getRestTemplate().exchange(
                    requestUri,
                    HttpMethod.GET,
                    null,
                    Resource.class
            );

            BpmnModelInstance model = Bpmn.readModelFromStream(Objects.requireNonNull(response.getBody()).getInputStream());
            Collection<SequenceFlow> sequenceFlows = model.getModelElementsByType(SequenceFlow.class);

            Map<String, Set<String>> activitySequences = new HashMap<>();
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                activitySequences.computeIfAbsent(sequenceFlow.getAttributeValue("sourceRef"), s -> new HashSet<>()).add(sequenceFlow.getAttributeValue("targetRef"));
            }
            return activitySequences;

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
