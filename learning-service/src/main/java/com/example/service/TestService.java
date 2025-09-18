package com.example.service;

import com.example.feign.UserServiceClient;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    private final UserServiceClient userClient;

    public TestService(UserServiceClient userClient) {
        this.userClient = userClient;
    }

    public String checkUserInstance() {
        return userClient.whoAmI();
    }
}
