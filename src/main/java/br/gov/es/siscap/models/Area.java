package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "area")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update area set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Area extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@ManyToOne
	@JoinColumn(name = "id_eixo", nullable = false)
	@SQLJoinTableRestriction("apagado = FALSE")
	private Eixo eixo;

	public Area(Long id) {
		this.setId(id);
	}
}
