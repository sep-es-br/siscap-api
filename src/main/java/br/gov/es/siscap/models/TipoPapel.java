package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tipo_papel")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update tipo_papel set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class TipoPapel extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_papel_id_gen")
	@SequenceGenerator(name = "tipo_papel_id_gen", sequenceName = "tipo_papel_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo", nullable = false)
	private String tipo;

	public TipoPapel(Long id) {
		this.setId(id);
	}
}