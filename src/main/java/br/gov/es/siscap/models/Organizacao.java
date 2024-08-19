package br.gov.es.siscap.models;

import br.gov.es.siscap.form.OrganizacaoForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "organizacao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update organizacao set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Organizacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome")
	private String nome;

	@Column(name = "nome_fantasia")
	private String nomeFantasia;

	@Column(name = "cnpj")
	private String cnpj;

	@Column(name = "telefone")
	private String telefone;

	@Column(name = "email")
	private String email;

	@Column(name = "site")
	private String site;

	@Column(name = "nome_imagem")
	private String nomeImagem;

	@OneToOne
	@JoinColumn(name = "organizacao_pai")
	@SQLJoinTableRestriction("apagado = FALSE")
	private Organizacao organizacaoPai;

	@ManyToOne
	@SQLJoinTableRestriction("apagado = FALSE")
	@JoinColumn(name = "status")
	private Status status;

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

	@OneToMany(mappedBy = "organizacao")
	private Set<PessoaOrganizacao> pessoaOrganizacaoSet;

	@DateTimeFormat
	@Column(name = "criado_em")
	private LocalDateTime criadoEm;

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@Column(name = "apagado")
	private boolean apagado = Boolean.FALSE;

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
		this.cidade = form.idCidade() != null ? new Cidade(form.idCidade()) : null;
		this.estado = form.idEstado() != null ? new Estado(form.idEstado()) : null;
		this.pais = new Pais(form.idPais());
		this.tipoOrganizacao = new TipoOrganizacao(form.idTipoOrganizacao());
		this.criadoEm = LocalDateTime.now();
	}

	public void atualizar(OrganizacaoForm form) {
		this.nome = form.nome();
		this.nomeFantasia = form.abreviatura();
		this.cnpj = form.cnpj();
		this.telefone = form.telefone();
		this.email = form.email();
		this.site = form.site();
		this.organizacaoPai = form.idOrganizacaoPai() != null ? new Organizacao(form.idOrganizacaoPai()) : null;
		this.cidade = form.idCidade() != null ? new Cidade(form.idCidade()) : null;
		this.estado = form.idEstado() != null ? new Estado(form.idEstado()) : null;
		this.pais = form.idPais() != null ? new Pais(form.idPais()) : null;
		this.tipoOrganizacao = form.idTipoOrganizacao() != null ? new TipoOrganizacao(form.idTipoOrganizacao()) : null;
		this.setAtualizadoEm(LocalDateTime.now());
	}

	public void atualizarImagemPerfil(String nomeImagem) {
		this.nomeImagem = nomeImagem;
	}


	public void apagar() {
		this.atualizadoEm = LocalDateTime.now();
		this.cnpj = null;
		this.nomeImagem = null;
	}
}
