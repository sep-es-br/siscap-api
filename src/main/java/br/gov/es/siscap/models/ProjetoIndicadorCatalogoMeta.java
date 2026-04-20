package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "projeto_indicador_catalogo_meta")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_indicador_catalogo_meta set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class ProjetoIndicadorCatalogoMeta extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_indicador_catalogo_meta_id_gen")
	@SequenceGenerator(name = "indicador_catalogo_id_gen", sequenceName = "projeto_indicador_catalogo_meta_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "ano", nullable = false)
	private Integer ano;

	@Column(name = "valor", nullable = false)
	private String valor;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto_indicador", nullable = false)
	private ProjetoIndicador projetoIndicador;

}
