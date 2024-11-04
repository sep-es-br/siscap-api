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
@Table(name = "localidade")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE localidade SET apagado = true WHERE id = ?")
@SQLRestriction("apagado = false")
public class Localidade extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "localidade_id_gen")
	@SequenceGenerator(name = "localidade_id_gen", sequenceName = "localidade_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "localidade_pai")
	private Localidade localidadePai;

	@Size(max = 255)
	@NotNull
	@Column(name = "nome", nullable = false)
	private String nome;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo", nullable = false)
	private String tipo;

	public Localidade(Long id) {
		this.setId(id);
	}
}