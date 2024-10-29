package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tipo_status")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update tipo_status set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class TipoStatus extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "tipo", nullable = false)
	private String tipo;

	public TipoStatus(Long id) {
		this.setId(id);
	}
}