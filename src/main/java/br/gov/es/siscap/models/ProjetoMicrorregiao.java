package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.RateioMicrorregiaoDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "projeto_microrregiao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_microrregiao set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class ProjetoMicrorregiao extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_microrregiao_id_gen")
	@SequenceGenerator(name = "projeto_microrregiao_id_gen", sequenceName = "projeto_microrregiao_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_microrregiao", nullable = false)
	private Microrregiao microrregiao;

	@NotNull
	@Column(name = "quantia", precision = 25, scale = 2)
	private BigDecimal quantia;

	@NotNull
	@Column(name = "percentual", precision = 5, scale = 2)
	private BigDecimal percentual;

	public ProjetoMicrorregiao(Projeto projeto, RateioMicrorregiaoDto rateioMicrorregiaoDto) {
		super();
		this.setProjeto(projeto);
		this.setMicrorregiao(new Microrregiao(rateioMicrorregiaoDto.idMicrorregiao()));
		this.setQuantia(rateioMicrorregiaoDto.quantia());
		this.setPercentual(rateioMicrorregiaoDto.percentual());
	}

	public void apagar() {
		super.apagarHistorico();
	}

	public boolean compararIdMicrorregiaoComRateioMicrorregiaoDto(RateioMicrorregiaoDto rateioMicrorregiaoDto) {
		return this.getMicrorregiao().getId().equals(rateioMicrorregiaoDto.idMicrorregiao());
	}
}
