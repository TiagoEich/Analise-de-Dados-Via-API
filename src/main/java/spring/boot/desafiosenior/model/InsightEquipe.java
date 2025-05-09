package spring.boot.desafiosenior.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsightEquipe {

    private String nome;
    private int membros;
    private int lideres;
    private int projetosConcluidos;
    private double percentualAtivos;
}