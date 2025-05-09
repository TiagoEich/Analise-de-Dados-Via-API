package spring.boot.desafiosenior.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import spring.boot.desafiosenior.model.Usuario;
import spring.boot.desafiosenior.repository.UsuarioRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JsonReaderService {

    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        carregarDadosIniciais();
    }

    public void carregarDadosIniciais() {
        try {
            List<Usuario> usuarios = lerArquivoJson();
            usuarioRepository.saveAll(usuarios);
            System.out.println("Dados carregados com sucesso: " + usuarios.size() + " usuários.");
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados iniciais: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Usuario> lerArquivoJson() throws IOException {
        ClassPathResource resource = new ClassPathResource("usuarios_1000.json");
        try (InputStream inputStream = resource.getInputStream()) {
            objectMapper.registerModule(new JavaTimeModule());
            Usuario[] usuariosArray = objectMapper.readValue(inputStream, Usuario[].class);
            return Arrays.asList(usuariosArray);
        }
    }
}