package br.gov.es.siscap.models;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class ControleHistorico {

	@NotNull
	@DateTimeFormat
	@Column(name = "criado_em", nullable = false)
	private LocalDateTime criadoEm = LocalDateTime.now();

	@Column(name = "atualizado_em")
	@DateTimeFormat
	private LocalDateTime atualizadoEm;

	@NotNull
	@Column(name = "apagado", nullable = false)
	private Boolean apagado = Boolean.FALSE;

	public void atualizarHistorico() {
		this.setAtualizadoEm(LocalDateTime.now());
	}

	public void apagarHistorico() {
		this.atualizarHistorico();
		this.setApagado(Boolean.TRUE);
	}
}
