package com._wellness.wellness.controller;

import com._wellness.wellness.entity.SensorReading;
import com._wellness.wellness.model.SensorRequest;
import com._wellness.wellness.repository.SensorRepository;
import com._wellness.wellness.security.EncryptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorRepository repository;
    private final EncryptionService encryption;

    public SensorController(SensorRepository repository, EncryptionService encryption) {
        this.repository = repository;
        this.encryption = encryption;
    }

    @PostMapping("/readings")
    public ResponseEntity<Map<String, String>> receiveReading(
            @Valid @RequestBody SensorRequest request) {

        Instant recordedAt = Instant.ofEpochMilli(request.getTimestamp());
        Instant now = Instant.now();

        if (recordedAt.isAfter(now.plusSeconds(300))
                || recordedAt.isBefore(now.minus(Duration.ofDays(1)))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid request"));
        }

        SensorReading reading = new SensorReading();
        reading.setUserId("demo-user");
        reading.setDataType("HEART_RATE");
        reading.setEncryptedPayload(
                encryption.encrypt("{\"heartRate\":" + request.getHeartRate() + "}"));
        reading.setRecordedAt(recordedAt);
        repository.save(reading);

        return ResponseEntity.ok(Map.of("message", "Sensor reading received successfully"));
    }
}