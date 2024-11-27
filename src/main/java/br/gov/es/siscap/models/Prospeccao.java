package br.gov.es.siscap.models;

import br.gov.es.siscap.enums.StatusProspeccaoEnum;
import br.gov.es.siscap.enums.TipoProspeccaoEnum;
import br.gov.es.siscap.form.ProspeccaoForm;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "prospeccao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update prospeccao set apagado = true where id=?")
@SQLRestriction("apagado = false")
public class Prospeccao extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prospeccao_id_gen")
	@SequenceGenerator(name = "prospeccao_id_gen", sequenceName = "prospeccao_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_cartaconsulta", nullable = false)
	private CartaConsulta cartaConsulta;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_organizacao_prospectora", nullable = false)
	private Organizacao organizacaoProspectora;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pessoa_prospectora", nullable = false)
	private Pessoa pessoaProspectora;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_organizacao_prospectada", nullable = false)
	private Organizacao organizacaoProspectada;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo_prospeccao", nullable = false)
	private String tipoProspeccao;

	@Size(max = 255)
	@NotNull
	@Column(name = "status_prospeccao", nullable = false)
	private String statusProspeccao;

	@Column(name = "data_prospeccao")
	private LocalDateTime dataProspeccao;

	@OneToMany(mappedBy = "prospeccao")
	private Set<ProspeccaoInteressado> prospeccaoInteressadoSet;

	public Prospeccao(ProspeccaoForm form) {
		this.setDadosObrigatorios(form);
		this.setTipoProspeccao(TipoProspeccaoEnum.ENVIO.getValue());
		this.setStatusProspeccao(StatusProspeccaoEnum.NAO_PROSPECTADO.getValue());
	}

	public void atualizar(ProspeccaoForm form) {
		this.setDadosObrigatorios(form);
		super.atualizarHistorico();
	}

	public void apagar() {
		super.apagarHistorico();
	}

	private void setDadosObrigatorios(ProspeccaoForm form) {
		this.setCartaConsulta(new CartaConsulta(form.idCartaConsulta()));
		this.setOrganizacaoProspectora(new Organizacao(form.idOrganizacaoProspectora()));
		this.setPessoaProspectora(new Pessoa(form.idPessoaProspectora()));
		this.setOrganizacaoProspectada(new Organizacao(form.idOrganizacaoProspectada()));
	}
}