package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "plano")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update plano set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Plano extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome", nullable = false)
	private String nome;

	public Plano(Long id) {
		this.setId(id);
	}
}
