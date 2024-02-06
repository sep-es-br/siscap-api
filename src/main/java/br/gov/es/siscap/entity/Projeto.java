package br.gov.es.siscap.entity;

import br.gov.es.siscap.form.ProjetoForm;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

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
    private String sigla;
    @Column(name = "title")
    private String titulo;
    @Column(name = "estimated_value")
    private BigInteger valorEstimado;
    @Column(name = "goal")
    private String objetivo;
    @Column(name = "specific_goal")
    private String objetivoEspecifico;
    @Column(name = "created_at")
    private LocalDateTime criadoEm;
    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm;
    @Column(name = "deleted_at")
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
