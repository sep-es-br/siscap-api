package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.RateioCidadeDto;
import br.gov.es.siscap.dto.RateioDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "projeto_cidade")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_cidade set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class ProjetoCidade extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_cidade_id_gen")
	@SequenceGenerator(name = "projeto_cidade_id_gen", sequenceName = "projeto_cidade_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_cidade", nullable = false)
	private Cidade cidade;

	@NotNull
	@Column(name = "quantia", precision = 25, scale = 2)
	private BigDecimal quantia;

	@NotNull
	@Column(name = "percentual", precision = 5, scale = 2)
	private BigDecimal percentual;

	public ProjetoCidade(Projeto projeto, RateioCidadeDto rateioCidadeDto) {
		super();
		this.setProjeto(projeto);
		this.setCidade(new Cidade(rateioCidadeDto.idCidade()));
		this.setQuantia(rateioCidadeDto.quantia());
		this.setPercentual(rateioCidadeDto.percentual());
	}

	public void apagar() {
		super.apagarHistorico();
	}

	public boolean compararIdCidadeComRateioCidadeDto(RateioCidadeDto rateioCidadeDto) {
		return this.getCidade().getId().equals(rateioCidadeDto.idCidade());
	}
}