package br.gov.es.siscap.models;

import br.gov.es.siscap.enums.StatusEnum;
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
	private Status status = new Status(StatusEnum.ATIVO.getValue());

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
	private LocalDateTime criadoEm = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@Column(name = "apagado")
	private boolean apagado = Boolean.FALSE;

	public Organizacao(Long id) {
		this.id = id;
	}

	public Organizacao(OrganizacaoForm form, String nomeImagem) {
		this.setDadosObrigatorios(form);
		this.setDadosOpcionais(form);
		this.atualizarImagemPerfil(nomeImagem);
	}

	public void atualizarOrganizacao(OrganizacaoForm form) {
		this.setDadosObrigatorios(form);
		this.setDadosOpcionais(form);
		this.setAtualizadoEm(LocalDateTime.now());
	}

	public void atualizarImagemPerfil(String nomeImagem) {
		this.setNomeImagem(nomeImagem);
	}

	public void apagarOrganizacao() {
		this.atualizadoEm = LocalDateTime.now();
		this.cnpj = null;
		this.nomeImagem = null;
	}

	private void setDadosObrigatorios(OrganizacaoForm form) {
		this.setNome(form.nome());
		this.setNomeFantasia(form.abreviatura());
		this.setTipoOrganizacao(new TipoOrganizacao(form.idTipoOrganizacao()));
		this.setPais(new Pais(form.idPais()));
	}

	private void setDadosOpcionais(OrganizacaoForm form) {
		this.setCnpj(form.cnpj() != null ? form.cnpj() : null);
		this.setOrganizacaoPai(form.idOrganizacaoPai() != null ? new Organizacao(form.idOrganizacaoPai()) : null);
		this.setEstado(form.idEstado() != null ? new Estado(form.idEstado()) : null);
		this.setCidade(form.idCidade() != null ? new Cidade(form.idCidade()) : null);
		this.setTelefone(form.telefone() != null ? form.telefone() : null);
		this.setEmail(form.email() != null ? form.email() : null);
		this.setSite(form.site() != null ? form.site() : null);
	}
}
