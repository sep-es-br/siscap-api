package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "pais")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update pais set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Pais extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "continente")
	private String continente;

	@Column(name = "subcontinente")
	private String subcontinente;

	@Column(name = "iso_alpha_3", nullable = false)
	private String isoAlpha3;

	@Column(name = "ddi")
	private String ddi;

	public Pais(Long id) {
		this.setId(id);
	}
}
