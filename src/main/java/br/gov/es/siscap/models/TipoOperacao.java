package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "tipo_operacao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update tipo_operacao set apagado = true where id = ?")
@SQLRestriction("apagado = false")
public class TipoOperacao {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_operacao_id_gen")
	@SequenceGenerator(name = "tipo_operacao_id_gen", sequenceName = "tipo_operacao_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo", nullable = false)
	private String tipo;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP")
	@DateTimeFormat
	@Column(name = "criado_em", nullable = false)
	private LocalDateTime criadoEm;

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@NotNull
	@ColumnDefault("false")
	@Column(name = "apagado", nullable = false)
	private Boolean apagado = false;

	public TipoOperacao(Long id) {
		this.setId(id);
	}
}