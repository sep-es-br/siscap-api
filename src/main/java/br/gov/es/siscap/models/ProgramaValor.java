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

@Entity
@Table(name = "programa_valor")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update programa_valor set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class ProgramaValor extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programa_valor_id_gen")
	@SequenceGenerator(name = "programa_valor_id_gen", sequenceName = "programa_valor_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_programa", nullable = false)
	private Programa programa;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tipo_valor", nullable = false)
	private TipoValor tipoValor;

	@Size(max = 3)
	@NotNull
	@Column(name = "moeda", nullable = false, length = 3)
	private String moeda;

	@NotNull
	@Column(name = "quantia", nullable = false, precision = 25, scale = 2)
	private BigDecimal quantia;

	@NotNull
	@DateTimeFormat
	@Column(name = "data_inicio", nullable = false)
	private LocalDateTime dataInicio = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "data_fim")
	private LocalDateTime dataFim;

	public ProgramaValor(Programa programa, ValorDto valorDto) {
		this.setPrograma(programa);
		this.setTipoValor(new TipoValor(valorDto.tipo()));
		this.setMoeda(valorDto.moeda());
		this.setQuantia(valorDto.quantia());
	}

	public void apagarProgramaValor() {
		this.setDataFim(LocalDateTime.now());
		super.apagarHistorico();
	}
}