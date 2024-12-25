package com.example.securityservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private final RestTemplate restTemplate = new RestTemplate();

    // URL-ы других микросервисов
    private static final String STREAMING_SERVICE_URL = "http://localhost:8081/streams";
    private static final String SESSION_SERVICE_URL = "http://localhost:8080/sessions";

    @PostMapping("/encrypt")
    public String encryptData(@RequestBody EncryptionRequest request) {
        // Простая эмуляция шифрования
        return new StringBuilder(request.getData()).reverse().toString();
    }

    @PostMapping("/decrypt")
    public String decryptData(@RequestBody EncryptionRequest request) {
        // Простая эмуляция расшифровки
        return new StringBuilder(request.getData()).reverse().toString();
    }

    @GetMapping("/status")
    public ResponseEntity<String> getSecurityStatus() {
        return ResponseEntity.ok("Security Service is running.");
    }

    @GetMapping("/check-session/{sessionId}")
    public boolean checkSession(@PathVariable String sessionId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(SESSION_SERVICE_URL + "/" + sessionId, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/validate-stream")
    public boolean validateStream(@RequestBody StreamValidationRequest request) {
        try {
            // Проверяем существование потока в Streaming Data Service
            ResponseEntity<Map> streamResponse = restTemplate.getForEntity(STREAMING_SERVICE_URL + "/" + request.getStreamId(), Map.class);

            // Проверяем валидность сессии
            boolean sessionValid = checkSession(request.getSessionId());

            return streamResponse.getStatusCode().is2xxSuccessful() && sessionValid;
        } catch (Exception e) {
            return false;
        }
    }
}
