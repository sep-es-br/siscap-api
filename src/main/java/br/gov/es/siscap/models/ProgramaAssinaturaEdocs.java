package br.gov.es.siscap.models;

import br.gov.es.siscap.enums.TipoStatusAssinaturaEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "programa_assinatura_documento_edocs")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update programa_assinatura_documento_edocs set apagado = true where id = ?")
@SQLRestriction("apagado = FALSE")
@ToString
public class ProgramaAssinaturaEdocs extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prog_assinatura_edocs_id_gen")
	@SequenceGenerator(
			name = "prog_assinatura_edocs_id_gen",
			sequenceName = "programa_assinatura_documento_edocs_id_seq",
			allocationSize = 1
	)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_programa", nullable = false)
	private Programa programa;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;

	@NotNull
	@Column(name = "status_assinatura", nullable = false)
	private Integer statusAssinatura;

	@DateTimeFormat
	@Column(name = "data_assinatura")
	private LocalDateTime dataAssinatura;

	@Column(name = "criado_em", nullable = false, updatable = false)
	private LocalDateTime criadoEm = LocalDateTime.now();

	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	public ProgramaAssinaturaEdocs(Programa programa, Pessoa pessoa) {
		this.programa = programa;
		this.pessoa = pessoa;
		this.statusAssinatura = TipoStatusAssinaturaEnum.PENDENTE.getValue();
		this.criadoEm = LocalDateTime.now();
	}

	public void marcarComoAssinado() {
		this.statusAssinatura = TipoStatusAssinaturaEnum.ASSINADO.getValue();
		this.dataAssinatura = LocalDateTime.now();
		this.atualizadoEm = LocalDateTime.now();
		super.atualizarHistorico();
	}

	public void marcarComoErro() {
		this.statusAssinatura = TipoStatusAssinaturaEnum.ERRO.getValue();
		this.atualizadoEm = LocalDateTime.now();
		super.atualizarHistorico();
	}

	public void apagar() {
		this.atualizadoEm = LocalDateTime.now();
		super.apagarHistorico();
	}
    
}
