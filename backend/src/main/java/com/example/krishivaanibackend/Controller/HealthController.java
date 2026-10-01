package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.dto.ApiResponse;
import com.example.krishivaanibackend.service.HealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
    private final HealthService healthService;
    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

//    GET /api/v1/health - Check the health of the application
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String >>> getHealth(){
        Map<String,String > data = Map.of(
                "status","UP",
                "message",healthService.getStatus()
        );
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
