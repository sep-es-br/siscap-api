package br.gov.es.siscap.models;

import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.form.ProjetoUpdateForm;
import jakarta.persistence.*;
import lombok.Getter;
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
    @JoinColumn(name = "id_entidade")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Entidade entidade;
    private String situacaoProblema;
    private String solucoesPropostas;
    private String impactos;
    private String arranjosInstitucionais;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "projeto_microrregiao",
            joinColumns = {@JoinColumn(name = "id_projeto")},
            inverseJoinColumns = @JoinColumn(name = "id_microrregiao"))
    private List<Microrregiao> microrregioes;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "projeto_pessoa",
            joinColumns = {@JoinColumn(name = "id_projeto")},
            inverseJoinColumns = @JoinColumn(name = "id_pessoa"))
    private List<Pessoa> equipeElaboracao;
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
        this.entidade = new Entidade(form.idEntidade());
        this.situacaoProblema = form.situacaoProblema();
        this.solucoesPropostas = form.solucoesPropostas();
        this.impactos = form.impactos();
        this.arranjosInstitucionais = form.arranjosInstitucionais();
        this.microrregioes = form.idMicrorregioes().stream().map(Microrregiao::new).toList();
        this.equipeElaboracao = form.idPessoasEquipeElab().stream().map(Pessoa::new).toList();
        this.criadoEm = LocalDateTime.now();
        this.apagado = Boolean.FALSE;
    }

    public void atualizarProjeto(ProjetoUpdateForm form) {
        if (form.sigla() != null)
            this.sigla = form.sigla();
        if (form.titulo() != null)
            this.titulo = form.titulo();
        if (form.idEntidade() != null)
            this.entidade = new Entidade(form.idEntidade());
        if (form.valorEstimado() != null)
            this.valorEstimado = form.valorEstimado();
        if (form.idMicrorregioes() != null && !form.idMicrorregioes().isEmpty())
            this.microrregioes = form.idMicrorregioes()
                    .stream().map(Microrregiao::new).collect(Collectors.toList());
        if (form.objetivo() != null)
            this.objetivo = form.objetivo();
        if (form.objetivoEspecifico() != null)
            this.objetivoEspecifico = form.objetivoEspecifico();
        if (form.situacaoProblema() != null)
            this.situacaoProblema = form.situacaoProblema();
        if (form.solucoesPropostas() != null)
            this.solucoesPropostas = form.solucoesPropostas();
        if (form.impactos() != null)
            this.impactos = form.impactos();
        if (form.arranjosInstitucionais() != null)
            this.arranjosInstitucionais = form.arranjosInstitucionais();
        if (form.idPessoasEquipeElab() != null && !form.idPessoasEquipeElab().isEmpty())
            this.equipeElaboracao = form.idPessoasEquipeElab().stream().map(Pessoa::new).collect(Collectors.toList());
        this.atualizadoEm = LocalDateTime.now();
    }

    public void apagar() {
        this.sigla = null;
        this.atualizadoEm = LocalDateTime.now();
    }

    public Long getIdEixo() {
        return this.area.getEixo() != null ? this.area.getEixo().getId() : null;
    }

    public Long getIdPlano() {
        if (getIdEixo() == null)
            return null;
        return this.area.getEixo().getPlano() != null ? this.area.getEixo().getPlano().getId() : null;
    }

}
