package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "valor")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update valor set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Valor extends ControleHistorico {

	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo", nullable = false)
	private String tipo;

	@OneToMany(mappedBy = "valor", fetch = FetchType.LAZY)
	private Set<ProgramaValor> programaValorSet;

//	@DateTimeFormat
//	@Column(name = "data_inicio")
//	private LocalDateTime dataInicio = LocalDateTime.now();
//
//	@DateTimeFormat
//	@Column(name = "data_fim")
//	private LocalDateTime dataFim;
//
//	@DateTimeFormat
//	@Column(name = "criado_em")
//	private LocalDateTime criadoEm = LocalDateTime.now();
//
//	@DateTimeFormat
//	@Column(name = "atualizado_em")
//	private LocalDateTime atualizadoEm;
//
//	@Column(name = "apagado")
//	private Boolean apagado = Boolean.FALSE;

	public Valor(Long id) {
		super();
		this.id = id;
	}

}