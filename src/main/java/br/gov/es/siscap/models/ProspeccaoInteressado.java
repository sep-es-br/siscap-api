package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.InteressadoDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "prospeccao_interessado")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update prospeccao_interessado set apagado = true where id=?")
@SQLRestriction("apagado = false")
public class ProspeccaoInteressado extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prospeccao_interessado_id_gen")
	@SequenceGenerator(name = "prospeccao_interessado_id_gen", sequenceName = "prospeccao_interessado_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_prospeccao", nullable = false)
	private Prospeccao prospeccao;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;

	@Size(max = 255)
	@NotNull
	@Column(name = "email_prospeccao", nullable = false)
	private String emailProspeccao;


	public ProspeccaoInteressado(Prospeccao prospeccao, InteressadoDto interessadoDto) {
		this.setProspeccao(prospeccao);
		this.setPessoa(new Pessoa(interessadoDto.idInteressado()));
		this.setEmailProspeccao(interessadoDto.emailInteressado());
	}

	public void atualizarProspeccaoInteressado(InteressadoDto interessadoDto) {
		this.setEmailProspeccao(interessadoDto.emailInteressado());
		super.atualizarHistorico();
	}

	public void apagarProspeccaoInteressado() {
		super.apagarHistorico();
	}
}