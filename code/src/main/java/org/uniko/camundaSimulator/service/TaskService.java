package org.uniko.camundaSimulator.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.uniko.camundaSimulator.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class TaskService implements InitializingBean {

    private static final String SEARCH_TASK_PATH = "/v1/tasks/search";
    private static final String ASSIGN_TASK_PATH = "/v1/tasks/{taskId}/assign";
    private static final String COMPLETE_TASK_PATH = "/v1/tasks/{taskId}/complete";
    private static TaskService taskServiceInstance;

    @Autowired
    private WebClient webClient;
    @Autowired
    private CamundaConfigurationService config;



    public static TaskService get() {
        return taskServiceInstance;
    }

    public ResponseEntity<List<Task>> searchTasks(TaskSearchRequest searchRequest) {
        try {
            URI requestUri = new URI(config.getTasklistAddress() + SEARCH_TASK_PATH);
            return config.getRestTemplate().exchange(
                    requestUri,
                    HttpMethod.POST,
                    new HttpEntity<>(searchRequest),
                    new ParameterizedTypeReference<>() {
                    }
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Flux<TaskResponse> assignTaskNonBlocking(String taskId, String assigneeName) {
        try {
            URI requestUri = new URI(config.getTasklistAddress() + ASSIGN_TASK_PATH.replace("{taskId}", taskId));
            Flux<TaskResponse> taskResponseFlux = webClient
                    .patch()
                    .uri(requestUri)
                    .body(Mono.just(new TaskAssignRequest(assigneeName, true)), TaskAssignRequest.class)
                    .retrieve()
                    .bodyToFlux(TaskResponse.class)
                    .retry(3);
            return taskResponseFlux;

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Flux<TaskResponse> completeTaskRequestNonBlocking(String taskId, TaskCompleteRequest request) {
        try {
            URI requestUri = new URI(config.getTasklistAddress() + COMPLETE_TASK_PATH.replace("{taskId}", taskId));
            return webClient
                    .patch()
                    .uri(requestUri)
                    .body(Mono.just(request), TaskCompleteRequest.class)
                    .retrieve()
                    .bodyToFlux(TaskResponse.class)
                    .retry(3);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        taskServiceInstance = this;
    }
}
