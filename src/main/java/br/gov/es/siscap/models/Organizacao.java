package br.gov.es.siscap.models;

import br.gov.es.siscap.form.OrganizacaoForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "organizacao")
@Getter
@SQLDelete(sql = "update organizacao set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Organizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String nomeFantasia;
    private String cnpj;
    private String telefone;
    private String email;
    private String site;
    private String nomeImagem;
    @OneToOne
    @JoinColumn(name = "organizacao_pai")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Organizacao organizacaoPai;
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
    @JoinColumn(name = "id_estado")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Estado estado;
    @ManyToOne
    @JoinColumn(name = "id_pais")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Pais pais;
    @ManyToOne
    @JoinColumn(name = "id_tipo_organizacao")
    @SQLJoinTableRestriction("apagado = FALSE")
    private TipoOrganizacao tipoOrganizacao;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;

    public Organizacao() {
    }

    public Organizacao(Long id) {
        this.id = id;
    }

    public Organizacao(OrganizacaoForm form, String nomeImagem) {
        this.nome = form.nome();
        this.nomeFantasia = form.abreviatura();
        this.cnpj = form.cnpj();
        this.telefone = form.telefone();
        this.email = form.email();
        this.site = form.site();
        this.nomeImagem = nomeImagem;
        this.organizacaoPai = form.idOrganizacaoPai() != null ? new Organizacao(form.idOrganizacaoPai()) : null;
        this.status = new Status(1L);
        this.pessoa = form.idPessoaResponsavel() != null ? new Pessoa(form.idPessoaResponsavel()) : null;
        this.cidade = form.idCidade() != null ? new Cidade(form.idCidade()) : null;
        this.estado = form.idEstado() != null ? new Estado(form.idEstado()) : null;
        this.pais = new Pais(form.idPais());
        this.tipoOrganizacao = new TipoOrganizacao(form.idTipoOrganizacao());
        this.criadoEm = LocalDateTime.now();
    }

    public void apagar() {
        this.atualizadoEm = LocalDateTime.now();
        this.cnpj = null;
    }

    public void atualizar(OrganizacaoForm form) {
        this.nome = form.nome();
        this.nomeFantasia = form.abreviatura();
        this.cnpj = form.cnpj();
        this.telefone = form.telefone();
        this.email = form.email();
        this.site = form.site();
        this.organizacaoPai = form.idOrganizacaoPai() != null ? new Organizacao(form.idOrganizacaoPai()) : null;
        this.pessoa = form.idPessoaResponsavel() != null ? new Pessoa(form.idPessoaResponsavel()) : null;
        this.cidade = form.idCidade() != null ? new Cidade(form.idCidade()) : null;
        this.estado = form.idEstado() != null ? new Estado(form.idEstado()) : null;
        this.pais = form.idPais() != null ? new Pais(form.idPais()) : null;
        this.tipoOrganizacao = form.idTipoOrganizacao() != null ? new TipoOrganizacao(form.idTipoOrganizacao()) : null;
        this.setAtualizadoEm(LocalDateTime.now());
    }

    public void atualizarImagemPerfil(String nomeImagem) {
        this.nomeImagem = nomeImagem;
    }
}
