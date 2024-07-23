package org.uniko.camundaSimulator.config;

import io.camunda.common.auth.TokenResponse;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.*;
import org.uniko.camundaSimulator.service.CamundaConfigurationService;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class WebConfig {

    static final Logger LOGGER = Logger.getLogger(WebConfig.class.getPackageName());
    final static private String TOKEN_PATH = "/auth/realms/camunda-platform/protocol/openid-connect/token";
    private static String session;

    @Autowired
    private CamundaConfigurationService config;

    @Bean("webClient")
    public WebClient webClient(ExchangeFilterFunction authRetryFilter) {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder().exchangeStrategies(strategies).filter(cookieFilter()).filter(authRetryFilter).build();
    }

    @Bean
    public ExchangeFilterFunction cookieFilter() {
        return ((request, next) -> next
                .exchange(ClientRequest.from(request).headers(h -> h.setBearerAuth(session)).build()));
    }

    // intercepts the response of the server if session status is "unauthorized" ->
    // creates a new session
    @Bean
    public ExchangeFilterFunction authRetryFilter() {
        return (request, next) -> next.exchange(request)
                .flatMap((Function<ClientResponse, Mono<ClientResponse>>) clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
                        return createSession().map(token -> session = token)
                                .map(token -> ClientRequest.from(request).headers(h -> h.setBearerAuth(session)).build())
                                .flatMap(next::exchange);
                    } else {
                        return Mono.just(clientResponse);
                    }
                });
    }

    private Mono<String> createSession() {
        WebClient authClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        return authClient.post()
                .uri(config.getKeycloakAddress() + TOKEN_PATH)
                .body(BodyInserters.fromFormData("client_id", config.getClientId())
                        .with("client_secret", config.getClientSecret())
                        .with("grant_type", "client_credentials"))
                .retrieve().toEntity(TokenResponse.class).filter(entity -> entity.getStatusCode().is2xxSuccessful())
                .doOnSuccess(entity -> LOGGER.log(Level.INFO, "Session created successfully."))
                .flatMap(entity -> Mono.justOrEmpty(entity.getBody().getAccessToken()))
                .doOnError(e -> LOGGER.log(Level.SEVERE,
                        "Error while creating session. Check url, username and password. Application will be terminated.",
                        e));
    }
}
