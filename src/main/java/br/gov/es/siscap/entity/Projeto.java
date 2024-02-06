package br.gov.es.siscap.entity;

import br.gov.es.siscap.form.ProjetoForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "project")
@Getter
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "acronym")
    @Setter
    private String sigla;
    @Column(name = "title")
    @Setter
    private String titulo;
    @Column(name = "estimated_value")
    @Setter
    private BigInteger valorEstimado;
    @Column(name = "goal")
    @Setter
    private String objetivo;
    @Column(name = "specific_goal")
    @Setter
    private String objetivoEspecifico;
    @Column(name = "created_at")
    private LocalDateTime criadoEm;
    @Column(name = "updated_at")
    @Setter
    private LocalDateTime atualizadoEm;
    @Column(name = "deleted_at")
    @Setter
    private LocalDateTime apagadoEm;

    public Projeto() {
    }

    public Projeto(ProjetoForm form) {
        this.sigla = form.sigla();
        this.titulo = form.titulo();
        this.valorEstimado = form.valorEstimado();
        this.objetivo = form.objetivo();
        this.objetivoEspecifico = form.objetivoEspecifico();
        this.criadoEm = LocalDateTime.now();
    }
}
