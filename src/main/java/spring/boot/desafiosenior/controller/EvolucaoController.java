package spring.boot.desafiosenior.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EvolucaoController {

    ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
            .build();

    private final WebClient client = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .exchangeStrategies(strategies)
            .build();

    @GetMapping("/evaluation")
    public Map<String, Object> evaluate() {
        List<String> endPoints = List.of(
                "/superusers",
                "/top-countries",
                "/team-insights",
                "/active-users-per-day"
        );

        Map<String, Object> results = new LinkedHashMap<>();

        endPoints.forEach(ep -> {
            long start = System.currentTimeMillis();

            try {
                ResponseEntity<String> resp = client.get()
                        .uri(ep)
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> {
                            System.err.println("Erro ao acessar " + ep + ": " + e.getMessage());
                            return Mono.empty();
                        })
                        .block();

                long time = System.currentTimeMillis() - start;

                Map<String, Object> resultDetails = new LinkedHashMap<>();
                resultDetails.put("status", resp == null ? 0 : resp.getStatusCode().value());
                resultDetails.put("time_ms", time);
                resultDetails.put("valid_response", resp != null);

                results.put(ep, resultDetails);
            } catch (Exception e) {
                long time = System.currentTimeMillis() - start;

                Map<String, Object> resultDetails = new LinkedHashMap<>();
                resultDetails.put("status", 0);
                resultDetails.put("time_ms", time);
                resultDetails.put("valid_response", false);
                resultDetails.put("error", e.getMessage());

                results.put(ep, resultDetails);
            }
        });

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tested_endpoints", results);
        return response;
    }
}