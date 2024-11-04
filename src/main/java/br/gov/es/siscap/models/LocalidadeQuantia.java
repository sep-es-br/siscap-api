package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.RateioDto;
import br.gov.es.siscap.dto.ValorDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "localidade_quantia")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update localidade_quantia set apagado = true where id=?")
@SQLRestriction("apagado = false")
public class LocalidadeQuantia extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "localidade_quantia_id_gen")
	@SequenceGenerator(name = "localidade_quantia_id_gen", sequenceName = "localidade_quantia_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_localidade", nullable = false)
	private Localidade localidade;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tipo_valor", nullable = false)
	private TipoValor tipoValor;

	@NotNull
	@Column(name = "quantia", nullable = false, precision = 25, scale = 2)
	private BigDecimal quantia;

	@Size(max = 3)
	@NotNull
	@Column(name = "moeda", nullable = false, length = 3)
	private String moeda;

	public LocalidadeQuantia(Projeto projeto, ValorDto valorDto, RateioDto rateioDto) {
		this.setProjeto(projeto);
		this.setLocalidade(new Localidade(rateioDto.idLocalidade()));
		this.setTipoValor(new TipoValor(valorDto.tipo()));
		this.setMoeda(valorDto.moeda());
		this.setQuantia(rateioDto.quantia());
	}

	public void atualizarLocalidadeQuantia(ValorDto valorDto, RateioDto rateioDto) {
		this.setTipoValor(new TipoValor(valorDto.tipo()));
		this.setMoeda(valorDto.moeda());
		this.setQuantia(rateioDto.quantia());
		super.atualizarHistorico();
	}

	public void apagarLocalidadeQuantia() {
		super.apagarHistorico();
	}
}