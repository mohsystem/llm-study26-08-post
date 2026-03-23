package com.um.springbootprojstructure.service;

public interface NotificationGatewayClient {
    void sendOtp(String phoneNumber, String message);
}
