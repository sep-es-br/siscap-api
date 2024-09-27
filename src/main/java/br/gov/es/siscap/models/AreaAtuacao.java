package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "area_atuacao")
@NoArgsConstructor
@Getter
@Setter
public class AreaAtuacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome", nullable = false)
	private String nome;

	public AreaAtuacao(Long id) {
		this.setId(id);
	}
}
