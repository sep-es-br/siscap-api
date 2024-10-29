package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tipo_valor")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update tipo_valor set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class TipoValor extends ControleHistorico {

	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo", nullable = false)
	private String tipo;

	public TipoValor(Long id) {
		this.setId(id);
	}
}