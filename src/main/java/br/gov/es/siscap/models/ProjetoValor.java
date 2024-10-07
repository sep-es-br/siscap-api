package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.ValorDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "projeto_valor")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_valor set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class ProjetoValor extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_valor_id_gen")
	@SequenceGenerator(name = "projeto_valor_id_gen", sequenceName = "projeto_valor_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_valor", nullable = false)
	private Valor valor;

	@Size(max = 3)
	@NotNull
	@Column(name = "moeda", nullable = false, length = 3)
	private String moeda;

	@NotNull
	@Column(name = "quantia", nullable = false, precision = 25, scale = 2)
	private BigDecimal quantia;

	@DateTimeFormat
	@Column(name = "data_inicio")
	private LocalDateTime dataInicio = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "data_fim")
	private LocalDateTime dataFim;

	public ProjetoValor(Projeto projeto, ValorDto valorDto) {
		this.setProjeto(projeto);
		this.setValor(new Valor(valorDto.tipo()));
		this.setMoeda(valorDto.moeda());
		this.setQuantia(valorDto.quantia());
	}

	public void apagarProjetoValor() {
		this.setDataFim(LocalDateTime.now());
		super.apagarHistorico();
	}

	public boolean compararProjetoValorComValorDto(ValorDto valorDto) {
		return (
					Objects.equals(this.getValor().getId(), valorDto.tipo()) &&
								Objects.equals(this.getMoeda(), valorDto.moeda()) &&
								Objects.equals(this.getQuantia(), valorDto.quantia())
		);
	}
}