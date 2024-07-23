package org.uniko.camundaSimulator.service;

import io.camunda.common.auth.TokenResponse;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class BearerSessionRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    final static private String TOKEN_PATH = "/auth/realms/camunda-platform/protocol/openid-connect/token";
    private final String hostAddress;
    private final String clientId;
    private final String clientSecret;
    private String token;

    BearerSessionRestTemplateInterceptor(String hostAddress, String clientId, String clientSecret) {
        this.hostAddress = hostAddress;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        if (this.token == null) createToken();

        request.getHeaders().setBearerAuth(this.token);
        ClientHttpResponse response = execution.execute(request, body);

        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            createToken();
            response = execution.execute(request, body);
        }
        return response;
    }

    private void createToken() {

        try {
            String loginUrl = hostAddress + TOKEN_PATH;

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("client_id", this.clientId);
            formData.add("client_secret", this.clientSecret);
            formData.add("grant_type", "client_credentials");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, requestHeaders);

            HttpEntity<TokenResponse> response = new RestTemplate().exchange(loginUrl, HttpMethod.POST, request, TokenResponse.class);

            this.token = response.getBody().getAccessToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
