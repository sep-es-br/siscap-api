package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;

@Entity
@Table(name = "valor")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update valor set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Valor extends ControleHistorico {

	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo", nullable = false)
	private String tipo;

	@OneToMany(mappedBy = "valor", fetch = FetchType.LAZY)
	private Set<ProgramaValor> programaValorSet;

	public Valor(Long id) {
		this.setId(id);
	}
}