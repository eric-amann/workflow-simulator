package org.uniko.camundaSimulator;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableZeebeClient
@EnableAsync
public class WorkflowSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowSimulatorApplication.class, args);
    }
}
