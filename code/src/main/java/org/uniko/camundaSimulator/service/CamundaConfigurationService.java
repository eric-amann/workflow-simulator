package org.uniko.camundaSimulator.service;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.ZeebeClientConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Getter
@Setter
@Component
public class CamundaConfigurationService {

    @Value("${camunda.operate.address}")
    private String operateAddress;
    @Value("${camunda.tasklist.address}")
    private String tasklistAddress;
    @Value("${camunda.elasticsearch.address}")
    private String elasticsearchAddress;
    @Value("${zeebe.client.broker.gateway-address}")
    private String zeebeGatewayAddress;
    @Value("${keycloak.address}")
    private String keycloakAddress;
    private String clientId;
    private String clientSecret;
    private RestTemplate restTemplate;
    private ZeebeClient zeebeClient;

    public CamundaConfigurationService() {
        setupRestTemplate();
        setupZeebeClient();
    }

    public void loadConfig(CamundaConfigurationService config) {
        if(config.getOperateAddress() != null) this.operateAddress = config.getOperateAddress();
        if(config.getTasklistAddress()!= null) this.tasklistAddress = config.getTasklistAddress();
        if(config.getElasticsearchAddress() != null) this.elasticsearchAddress = config.getElasticsearchAddress();
        if(config.getZeebeGatewayAddress() != null) this.zeebeGatewayAddress = config.getZeebeGatewayAddress();
        if(config.getKeycloakAddress() != null) this.keycloakAddress = config.getKeycloakAddress();
        if(config.getClientId() != null) this.clientId = config.getClientId();
        if(config.getClientSecret() != null) this.clientSecret = config.getClientSecret();

        setupRestTemplate();
        setupZeebeClient();
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
        setupRestTemplate();
        setupZeebeClient();
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        setupRestTemplate();
        setupZeebeClient();
    }

    private void setupRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        this.restTemplate = builder
                .interceptors(new BearerSessionRestTemplateInterceptor(keycloakAddress, clientId, clientSecret))
                .build();
    }

    private void setupZeebeClient() {

        ZeebeClientBuilder builder = ZeebeClient.newClientBuilder();
        builder.gatewayAddress(this.zeebeGatewayAddress).usePlaintext();
        this.zeebeClient = builder.build();
    }
}
