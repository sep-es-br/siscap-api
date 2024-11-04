package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "cidade")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update cidade set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Cidade extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_estado")
	@SQLJoinTableRestriction("apagado = FALSE")
	private Estado estado;

	@Column(name = "id_ibge", nullable = false)
	private String codigoIBGE;

	@Column(name = "nome", nullable = false)
	private String nome;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_microrregiao")
	@SQLJoinTableRestriction("apagado = FALSE")
	private Microrregiao microrregiao;

	public Cidade(Long id) {
		this.setId(id);
	}
}