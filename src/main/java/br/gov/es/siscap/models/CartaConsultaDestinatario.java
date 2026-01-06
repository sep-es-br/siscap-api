package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "cartaconsulta_destinatario")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update cartaconsulta_destinatario set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class CartaConsultaDestinatario extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cartaconsulta_destinatario_id_gen")
	@SequenceGenerator(name = "cartaconsulta_destinatario_id_gen", sequenceName = "cartaconsulta_destinatario_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_cartaconsulta", nullable = false)
	private CartaConsulta cartaConsulta;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_organizacao", nullable = false)
	private Organizacao organizacao;

	public CartaConsultaDestinatario(CartaConsulta cartaConsulta, Organizacao organizacao) {
		this.setCartaConsulta(cartaConsulta);
		this.setOrganizacao(organizacao);
	}

	public void apagarCartaConsultaDestinatario() {
		super.apagarHistorico();
	}

	public Long getOrganizacaoId() {
		return this.getOrganizacao().getId();
	}

}