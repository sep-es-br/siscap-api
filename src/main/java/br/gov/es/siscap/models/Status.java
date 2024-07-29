package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "status")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update status set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Status {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "status")
	private String status;

	@DateTimeFormat
	@Column(name = "criado_em")
	private LocalDateTime criadoEm;

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@Column(name = "apagado")
	private boolean apagado = Boolean.FALSE;

	public Status(Long id) {
		this.id = id;
	}
}
