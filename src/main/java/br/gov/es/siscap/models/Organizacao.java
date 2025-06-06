package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.organogramawebapi.OrganogramaOrganizacaoInfoEssencialDto;
import br.gov.es.siscap.enums.TipoOrganizacaoEnum;
import br.gov.es.siscap.enums.TipoStatusEnum;
import br.gov.es.siscap.form.OrganizacaoForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;

@Entity
@Table(name = "organizacao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update organizacao set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Organizacao extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "nome_fantasia", nullable = false)
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

	@Column(name = "guid")
	private String guid;

	@OneToOne
	@JoinColumn(name = "organizacao_pai")
	@SQLJoinTableRestriction("apagado = FALSE")
	private Organizacao organizacaoPai;

	@ManyToOne
	@JoinColumn(name = "id_tipo_status", nullable = false)
	@SQLJoinTableRestriction("apagado = FALSE")
	private TipoStatus tipoStatus = new TipoStatus(TipoStatusEnum.ATIVO.getValue());

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
	@JoinColumn(name = "id_tipo_organizacao", nullable = false)
	@SQLJoinTableRestriction("apagado = FALSE")
	private TipoOrganizacao tipoOrganizacao;

	@OneToMany(mappedBy = "organizacao")
	private Set<PessoaOrganizacao> pessoaOrganizacaoSet;

	public Organizacao(Long id) {
		this.setId(id);
	}

	public Organizacao(OrganogramaOrganizacaoInfoEssencialDto organogramaOrganizacaoInfoEssencialDto) {
		this.setCnpj(organogramaOrganizacaoInfoEssencialDto.cnpj());
		this.setNomeFantasia(organogramaOrganizacaoInfoEssencialDto.sigla());
		this.setNome(organogramaOrganizacaoInfoEssencialDto.razaoSocial());
		this.setPais(new Pais(1L)); // Brasil
		this.setEstado(new Estado(18L)); // Espirito Santo
		this.setTipoOrganizacao(
					new TipoOrganizacao(
								organogramaOrganizacaoInfoEssencialDto.razaoSocial().contains("SECRETARIA")
											? TipoOrganizacaoEnum.SECRETARIA.getValue() : TipoOrganizacaoEnum.INSTITUICAO_PUBLICA.getValue()
					)
		);
		this.setGuid(organogramaOrganizacaoInfoEssencialDto.guid());
	}

	public Organizacao(OrganizacaoForm form, String nomeImagem) {
		this.setDadosObrigatorios(form);
		this.setDadosOpcionais(form);
		this.atualizarImagemPerfil(nomeImagem);
	}

	public void atualizarOrganizacao(OrganizacaoForm form) {
		this.setDadosObrigatorios(form);
		this.setDadosOpcionais(form);
		super.atualizarHistorico();
	}

	public void atualizarImagemPerfil(String nomeImagem) {
		this.setNomeImagem(nomeImagem);
	}

	public void apagarOrganizacao() {
		this.setCnpj(null);
		this.setNomeImagem(null);
		super.apagarHistorico();
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