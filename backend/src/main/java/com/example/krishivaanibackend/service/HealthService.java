package com.example.krishivaanibackend.service;

import org.springframework.stereotype.Service;

@Service
public class HealthService {
    public String getStatus() {
        return "KrishiVaani AI backend is running";
    }
}
