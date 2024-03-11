package br.gov.es.siscap.models;

import br.gov.es.siscap.form.EntidadeForm;
import br.gov.es.siscap.form.EntidadeUpdateForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "entidade")
@Getter
@SQLDelete(sql = "update entidade set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Entidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String abreviatura;
    private String cnpj;
    private String telefone;
    private String fax;
    private String email;
    private String site;
    private String nomeImagem;
    @OneToOne
    @JoinColumn(name = "entidade_pai")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Entidade entidadePai;
    @ManyToOne
    @SQLJoinTableRestriction("apagado = FALSE")
    @JoinColumn(name = "status")
    private Status status;
    @OneToOne
    @JoinColumn(name = "id_pessoa")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Pessoa pessoa;
    @ManyToOne
    @JoinColumn(name = "id_cidade")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Cidade cidade;
    @ManyToOne
    @JoinColumn(name = "id_pais")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Pais pais;
    @ManyToOne
    @JoinColumn(name = "id_tipo_entidade")
    @SQLJoinTableRestriction("apagado = FALSE")
    private TipoEntidade tipoEntidade;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;

    public Entidade() {
    }

    public Entidade(Long id) {
        this.id = id;
    }

    public Entidade(EntidadeForm form, String nomeImagem) {
        this.nome = form.nome();
        this.abreviatura = form.abreviatura();
        this.cnpj = form.cnpj();
        this.telefone = form.telefone();
        this.fax = form.fax();
        this.email = form.email();
        this.site = form.site();
        this.nomeImagem = nomeImagem;
        this.entidadePai = form.idEntidadePai() != null ? new Entidade(form.idEntidadePai()) : null;
        this.status = new Status(1L);
        this.pessoa = form.idPessoaResponsavel() != null ? new Pessoa(form.idPessoaResponsavel()) : null;
        this.cidade = form.idCidade() != null ? new Cidade(form.idCidade()) : null;
        this.pais = new Pais(form.idPais());
        this.tipoEntidade = new TipoEntidade(form.idTipoEntidade());
        this.criadoEm = LocalDateTime.now();
    }

    public void apagar() {
        this.atualizadoEm = LocalDateTime.now();
        this.cnpj = null;
    }

    public void atualizar(EntidadeUpdateForm form) {
        if (form.nome() != null)
            this.nome = form.nome();
        if (form.abreviatura() != null)
            this.abreviatura = form.abreviatura();
        if (form.cnpj() != null)
            this.cnpj = form.cnpj();
        if (form.telefone() != null)
            this.telefone = form.telefone();
        if (form.fax() != null)
            this.fax = form.fax();
        if (form.email() != null)
            this.email = form.email();
        if (form.site() != null)
            this.site = form.site();
        if (form.idEntidadePai() != null)
            this.entidadePai = new Entidade(form.idEntidadePai());
        if (form.idPessoaResponsavel() != null)
            this.pessoa = new Pessoa(form.idPessoaResponsavel());
        if (form.idCidade() != null)
            this.cidade = new Cidade(form.idCidade());
        if (form.idPais() != null)
            this.pais = new Pais(form.idPais());
        if (form.idTipoEntidade() != null)
            this.tipoEntidade = new TipoEntidade(form.idTipoEntidade());
        this.setAtualizadoEm(LocalDateTime.now());
    }

    public void atualizarImagemPerfil(String nomeImagem) {
        this.nomeImagem = nomeImagem;
    }
}
