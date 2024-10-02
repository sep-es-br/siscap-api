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
@Table(name = "equipe")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update equipe set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Equipe extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipe_id_gen")
	@SequenceGenerator(name = "equipe_id_gen", sequenceName = "equipe_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo", nullable = false)
	private String tipo;

	public Equipe(Long id) {
		this.setId(id);
	}
}