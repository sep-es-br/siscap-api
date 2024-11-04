package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "microrregiao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update microrregiao set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Microrregiao extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@OneToMany(mappedBy = "microrregiao")
	private List<Cidade> cidades;

	public Microrregiao(Long id) {
		this.setId(id);
	}
}