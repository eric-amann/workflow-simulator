package org.uniko.camundaSimulator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.uniko.camundaSimulator.service.CamundaConfigurationService;

@Configuration
@EnableElasticsearchRepositories("org.uniko.camundaSimulator.repository")
public class CamundaElasticsearchClientConfig extends ElasticsearchConfiguration {

    @Autowired
    private CamundaConfigurationService config;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(config.getElasticsearchAddress())
                .build();
    }
}
