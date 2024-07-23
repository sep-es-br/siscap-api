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

@Entity
@Table(name = "equipe")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update equipe set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Equipe {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipe_id_gen")
	@SequenceGenerator(name = "equipe_id_gen", sequenceName = "equipe_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo", nullable = false)
	private String tipo;

	@DateTimeFormat
	@Column(name = "criado_em")
	private LocalDateTime criadoEm;

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@Column(name = "apagado")
	private Boolean apagado;

	public Equipe(Long id) {
		this.id = id;
	}

}