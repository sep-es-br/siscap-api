package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tipo_organizacao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update tipo_organizacao set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class TipoOrganizacao extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "tipo", nullable = false)
	private String tipo;

	public TipoOrganizacao(Long id) {
		this.setId(id);
	}
}
