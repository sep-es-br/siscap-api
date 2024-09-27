package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "eixo")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update eixo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Eixo extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@ManyToOne
	@JoinColumn(name = "id_plano", nullable = false)
	@SQLJoinTableRestriction("apagado = FALSE")
	private Plano plano;

	public Eixo(Long id) {
		this.setId(id);
	}
}
