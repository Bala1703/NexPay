
package com.nexpay.agent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AgentController {

    @GetMapping("/api/agent/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "ai-build-investigation-agent",
                "status", "UP"
        );
    }
}
