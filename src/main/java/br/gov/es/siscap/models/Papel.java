package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Entity
@Table(name = "papel")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update papel set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Papel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "papel_id_gen")
	@SequenceGenerator(name = "papel_id_gen", sequenceName = "papel_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo", nullable = false)
	private String tipo;

	@Column(name = "criado_em")
	private Instant criadoEm;

	@Column(name = "atualizado_em")
	private Instant atualizadoEm;

	@Column(name = "apagado")
	private Boolean apagado;

	public Papel(Long id) {
		this.id = id;
	}
}