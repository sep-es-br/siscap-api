package br.gov.es.siscap.models;

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
public class ProjetoCidade {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_cidade_id_gen")
	@SequenceGenerator(name = "projeto_cidade_id_gen", sequenceName = "projeto_cidade_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_cidade", nullable = false)
	private Cidade cidade;

	@Column(name = "quantia", precision = 25, scale = 2)
	private BigDecimal quantia;

	@DateTimeFormat
	@Column(name = "data_inicio")
	private LocalDateTime dataInicio = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "data_fim")
	private LocalDateTime dataFim;

	@DateTimeFormat
	@Column(name = "criado_em")
	private LocalDateTime criadoEm = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@Column(name = "apagado")
	private Boolean apagado = Boolean.FALSE;

	public ProjetoCidade(Projeto projeto, RateioDto rateioDto) {
		this.setProjeto(projeto);
		this.setCidade(new Cidade(rateioDto.idCidade()));
		this.setQuantia(rateioDto.quantia());
	}

	public void apagar() {
		this.setDataFim(LocalDateTime.now());
		this.setAtualizadoEm(LocalDateTime.now());
		this.setApagado(true);
	}

	public boolean compararComRateioDto(RateioDto rateioDto) {
		return Objects.equals(this.getCidade().getId(), rateioDto.idCidade());
	}

}