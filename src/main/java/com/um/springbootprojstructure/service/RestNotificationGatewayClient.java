package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.SmsGatewayRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RestNotificationGatewayClient implements NotificationGatewayClient {

    private final RestClient restClient;

    public RestNotificationGatewayClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public void sendOtp(String phoneNumber, String message) {
        restClient.post()
                .uri("http://localhost:9999/sms/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SmsGatewayRequest(phoneNumber, message))
                .retrieve()
                .toBodilessEntity();
    }
}
