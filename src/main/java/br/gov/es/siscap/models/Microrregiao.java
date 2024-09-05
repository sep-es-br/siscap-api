package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "microrregiao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update microrregiao set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Microrregiao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome")
	private String nome;

	@OneToMany(mappedBy = "microrregiao")
	private List<Cidade> cidades;

	@OneToMany(mappedBy = "microrregiao")
	private Set<ProjetoMicrorregiao> projetoMicrorregiaoSet;

	@DateTimeFormat
	@Column(name = "criado_em")
	private LocalDateTime criadoEm;

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@Column(name = "apagado")
	private boolean apagado = Boolean.FALSE;


	public Microrregiao(Long id) {
		this.id = id;
	}
}
