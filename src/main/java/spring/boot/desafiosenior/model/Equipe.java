package spring.boot.desafiosenior.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Equipe {

    private String nome;
    private boolean lider;
    private List<Projeto> projetos;

}
