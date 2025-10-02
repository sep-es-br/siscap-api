package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(name = "projeto_complemento")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_complemento set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class ProjetoCamposComplementacao extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_complemento_id_gen")
	@SequenceGenerator(name = "projeto_complemento_id_gen", sequenceName = "projeto_complemento_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne()
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@Column(name = "campo", nullable = false)
	private String campo;

	@Column(name = "mensagem_complementacao", nullable = false)
	private String mensagemComplementacao;

	public ProjetoCamposComplementacao(Projeto projeto, ProjetoCamposComplementacaoDto complemento) {
		this.setProjeto(projeto);
		this.setId(complemento.idComplemento());
		this.setCampo(complemento.descricaoCampo());
		this.setMensagemComplementacao(complemento.descricaoComplemento());
	}

}