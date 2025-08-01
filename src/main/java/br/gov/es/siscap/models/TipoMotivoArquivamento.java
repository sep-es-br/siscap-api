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
@Table(name = "tipo_motivo_arquivamento")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update tipo_motivo_arquivamento set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class TipoMotivoArquivamento extends ControleHistorico {

	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 255)
	@NotNull
	@Column(name = "tipo", nullable = false)
	private String tipo;

	@Size(max = 4)
	@NotNull
	@Column(name = "codigo", nullable = false)
	private String codigo;

	@Size(max = 255)
	@NotNull
	@Column(name = "observacao", nullable = true)
	private String observacao;

	public TipoMotivoArquivamento(Long id) {
		this.setId(id);
	}

}