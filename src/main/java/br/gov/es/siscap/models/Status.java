package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "status")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update status set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Status extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "status", nullable = false)
	private String status;

	public Status(Long id) {
		this.setId(id);
	}
}
