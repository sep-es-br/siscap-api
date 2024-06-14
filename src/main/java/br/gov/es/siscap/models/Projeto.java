package br.gov.es.siscap.models;

import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.form.ProjetoFormUpdate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
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
    private String sigla;
    private String titulo;
    private BigDecimal valorEstimado;
    private String objetivo;
    private String objetivoEspecifico;
    @ManyToOne
    @SQLJoinTableRestriction("apagado = FALSE")
    @JoinColumn(name = "status")
    private Status status;
    @ManyToOne
    @JoinColumn(name = "id_organizacao")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Organizacao organizacao;
    private String situacaoProblema;
    private String solucoesPropostas;
    private String impactos;
    private String arranjosInstitucionais;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "projeto_microrregiao",
            joinColumns = {@JoinColumn(name = "id_projeto")},
            inverseJoinColumns = @JoinColumn(name = "id_microrregiao"))
    private List<Microrregiao> microrregioes;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projeto")
    @Setter
    private List<ProjetoPessoa> equipeElaboracao;
    @ManyToOne
    @JoinColumn(name = "id_area")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Area area;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    private boolean apagado;

    public Projeto() {
    }

    public Projeto(ProjetoForm form) {
        this.sigla = form.sigla();
        this.titulo = form.titulo();
        this.valorEstimado = form.valorEstimado();
        this.objetivo = form.objetivo();
        this.objetivoEspecifico = form.objetivoEspecifico();
        this.status = new Status(1L);
        this.organizacao = new Organizacao(form.idOrganizacao());
        this.situacaoProblema = form.situacaoProblema();
        this.solucoesPropostas = form.solucoesPropostas();
        this.impactos = form.impactos();
        this.arranjosInstitucionais = form.arranjosInstitucionais();
        this.microrregioes = form.idMicrorregioes().stream().map(Microrregiao::new).toList();
        this.criadoEm = LocalDateTime.now();
        this.apagado = Boolean.FALSE;
    }

    public Projeto(Long id) {
        this.id = id;
    }

    public void atualizarProjeto(ProjetoFormUpdate form) {
        this.sigla = form.sigla();
        this.titulo = form.titulo();
        this.organizacao = new Organizacao(form.idOrganizacao());
        this.valorEstimado = form.valorEstimado();
        this.microrregioes = form.idMicrorregioes()
                .stream().map(Microrregiao::new).collect(Collectors.toList());
        this.objetivo = form.objetivo();
        this.objetivoEspecifico = form.objetivoEspecifico();
        this.situacaoProblema = form.situacaoProblema();
        this.solucoesPropostas = form.solucoesPropostas();
        this.impactos = form.impactos();
        this.arranjosInstitucionais = form.arranjosInstitucionais();
        this.equipeElaboracao = form.equipeElab().stream().map(ProjetoPessoa::new).collect(Collectors.toList());
        this.atualizadoEm = LocalDateTime.now();
    }

    public void apagar() {
        this.sigla = null;
        this.atualizadoEm = LocalDateTime.now();
    }

}
