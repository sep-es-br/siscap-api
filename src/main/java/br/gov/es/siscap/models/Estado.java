package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "estado")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update estado set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Estado extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_pais", nullable = false)
	@SQLJoinTableRestriction("apagado = FALSE")
	private Pais pais;

	@Column(name = "id_ibge", nullable = false)
	private String codigoIBGE;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "sigla", nullable = false)
	private String sigla;

	public Estado(Long id) {
		this.setId(id);
	}
}
