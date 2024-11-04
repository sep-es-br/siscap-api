package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.opcoes.ObjetoOpcoesDto;
import br.gov.es.siscap.form.CartaConsultaForm;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@Entity
@Table(name = "cartaconsulta")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update cartaconsulta set apagado = true where id = ?")
@SQLRestriction("apagado = false")
public class CartaConsulta extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cartaconsulta_id_gen")
	@SequenceGenerator(name = "cartaconsulta_id_gen", sequenceName = "cartaconsulta_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "nome_documento", nullable = false, length = Integer.MAX_VALUE)
	private String nomeDocumento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto")
	private Projeto projeto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_programa")
	private Programa programa;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_operacao", nullable = false)
	private TipoOperacao tipoOperacao;

	public CartaConsulta(CartaConsultaForm form) {
		this.setCartaConsultaObjeto(form.objeto());
		this.setTipoOperacao(new TipoOperacao(form.operacao()));
	}

	public void atualizarCartaConsulta(CartaConsultaForm form) {
		this.setCartaConsultaObjeto(form.objeto());
		this.setTipoOperacao(new TipoOperacao(form.operacao()));
		super.atualizarHistorico();
	}

	public void apagarCartaConsulta() {
		super.apagarHistorico();
	}

	public String formatarDataCartaConsultaListaDto() {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("dd/MM/yyyy").toFormatter();

		return this.getCriadoEm().format(formatter);
	}

	public ObjetoOpcoesDto getCartaConsultaObjeto() {
		if (this.getProjeto() != null) return new ObjetoOpcoesDto(this.getProjeto());
		if (this.getPrograma() != null) return new ObjetoOpcoesDto(this.getPrograma());
		return null;
	}

	private void setCartaConsultaObjeto(ObjetoOpcoesDto formObjeto) {
		if (formObjeto.tipo().equals("Projeto")) {
			this.setProjeto(new Projeto(formObjeto.id()));
			if (this.getPrograma() != null) this.setPrograma(null);
		}

		if (formObjeto.tipo().equals("Programa")) {
			this.setPrograma(new Programa(formObjeto.id()));
			if (this.getProjeto() != null) this.setProjeto(null);
		}
	}

}