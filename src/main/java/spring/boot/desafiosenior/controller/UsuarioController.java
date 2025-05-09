package spring.boot.desafiosenior.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.boot.desafiosenior.model.Usuario;
import spring.boot.desafiosenior.repository.UsuarioRepository;
import spring.boot.desafiosenior.service.AnaliseService;
import spring.boot.desafiosenior.service.JsonReaderService;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository repo;
    private final AnaliseService svc;
    private final JsonReaderService jsonReaderService;

    private <T> Map<String, Object> timed(Supplier<T> supplier) {
        long start = System.currentTimeMillis();
        T data = supplier.get();
        long exec = System.currentTimeMillis() - start;
        return Map.of(
                "timestamp", Instant.now(),
                "execution_time_ms", exec,
                "data", data
        );
    }

    @PostMapping("/users")
    public Map<String, Object> carregar(@RequestBody List<Usuario> usuarios) {
        return timed(() -> {
                    repo.saveAll(usuarios);
                    return Map.of(
                            "mensagem", "Arquivo recebido com sucesso",
                            "total_usuarios", repo.findAll().size()
                    );
                }
        );
    }

    @GetMapping("/load-from-json")
    public Map<String, Object> carregarDeArquivo() {
        return timed(() -> {
            try {
                List<Usuario> usuarios = jsonReaderService.lerArquivoJson();
                repo.saveAll(usuarios);
                return Map.of(
                        "mensagem", "Arquivo JSON carregado com sucesso",
                        "total_usuarios", repo.findAll().size()
                );
            } catch (IOException e) {
                return Map.of(
                        "erro", "Falha ao carregar arquivo JSON",
                        "mensagem", e.getMessage()
                );
            }
        });
    }

    @GetMapping("/superusers")
    public Map<String, Object> superUsuarios() {
        return timed(svc::superUsuarios);
    }

    @GetMapping("/top-countries")
    public Map<String, Object> topPaises() {
        return timed(svc::topPaises);
    }

    @GetMapping("/team-insights")
    public Map<String, Object> insightsEquipe() {
        return timed(svc::insightEquipes);
    }

    @GetMapping("/active-users-per-day")
    public Map<String, Object> login(@RequestParam(defaultValue = "0") int min) {
        return timed(() -> svc.loginPorDia(min));
    }

    private ResponseEntity<?> ok(Object dados) {
        return ResponseEntity.ok(Map.of("data", dados));
    }
}