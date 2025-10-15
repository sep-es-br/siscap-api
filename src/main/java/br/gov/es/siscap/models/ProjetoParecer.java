package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.ProjetoParecerDto;
import br.gov.es.siscap.enums.StatusParecerEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(name = "projeto_parecer")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_parecer set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class ProjetoParecer extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_parecer_id_gen")
	@SequenceGenerator(name = "projeto_parecer_id_gen", sequenceName = "projeto_parecer_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne()
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@Column(name = "guid_unidade_organizacao", nullable = false)
	private String guidUnidadeOrganizacao;

	@Column(name = "texto_parecer", nullable = true, length=2000)
	private String textoParecer;

	@Column(name = "status_parecer", nullable = false)
	private StatusParecerEnum statusParecer;

	@Column(name = "data_envio", nullable = false)
	private LocalDateTime dataEnvio;

	@Column(name = "guid_documento_edocs", nullable = true, length=50)
	private String guidDocumentoEdocs;

	public ProjetoParecer(Projeto projeto, ProjetoParecerDto parecer) {
		this.setProjeto(projeto);
		this.setId(parecer.id());
		this.setGuidUnidadeOrganizacao(parecer.guidUnidadeOrganizacao());
		this.setTextoParecer(parecer.textoParecer());
		this.setStatusParecer(parecer.statusParecer());
		this.setDataEnvio(parecer.dataEnvio());
		this.setGuidDocumentoEdocs(parecer.guidDocumentoEdocs());
	}


}