package br.gov.es.siscap.models;

import br.gov.es.siscap.form.ProjetoForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "projeto")
@Getter
@SQLDelete(sql = "update projeto set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String sigla;
    @Setter
    private String titulo;
    @Setter
    private BigInteger valorEstimado;
    @Setter
    private String objetivo;
    @Setter
    private String objetivoEspecifico;
    @ManyToOne
    @Setter
    @SQLJoinTableRestriction("apagado = FALSE")
    @JoinColumn(name = "status")
    private Status status;
    @ManyToOne
    @JoinColumn(name = "id_entidade")
    @Setter
    @SQLJoinTableRestriction("apagado = FALSE")
    private Entidade entidade;
    @Setter
    private String situacaoProblema;
    @Setter
    private String solucoesPropostas;
    @Setter
    private String impactos;
    @Setter
    private String arranjosInstitucionais;
    @ManyToMany
    @JoinTable(name = "projeto_microrregiao",
            joinColumns = {@JoinColumn(name = "id_projeto"),},
            inverseJoinColumns = @JoinColumn(name = "id_microrregiao"))
    @Setter
    private List<Microrregiao> microregioes;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;

    public Projeto() {
    }

    public Projeto(ProjetoForm form) {
        this.sigla = form.sigla();
        this.titulo = form.titulo();
        this.valorEstimado = form.valorEstimado();
        this.objetivo = form.objetivo();
        this.objetivoEspecifico = form.objetivoEspecifico();
        this.status = new Status(1L);
        this.entidade = new Entidade(form.idEntidade());
        this.situacaoProblema = form.situacaoProblema();
        this.solucoesPropostas = form.solucoesPropostas();
        this.impactos = form.impactos();
        this.arranjosInstitucionais = form.arranjosInstitucionais();
        this.microregioes = form.idMicrorregioes().stream().map(Microrregiao::new).collect(Collectors.toList());
        this.criadoEm = LocalDateTime.now();
    }

}
