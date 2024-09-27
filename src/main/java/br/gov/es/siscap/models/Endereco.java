package br.gov.es.siscap.models;

import br.gov.es.siscap.form.EnderecoForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "endereco")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update endereco set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Endereco extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "rua")
	private String rua;

	@Column(name = "numero")
	private String numero;

	@Column(name = "bairro")
	private String bairro;

	@Column(name = "complemento")
	private String complemento;

	@Column(name = "codigo_postal")
	private String codigoPostal;

	@ManyToOne
	@JoinColumn(name = "id_cidade", nullable = false)
	@SQLJoinTableRestriction("apagado = FALSE")
	private Cidade cidade;

	public Endereco(EnderecoForm form) {
		this.setDadosEndereco(form);
	}

	public void atualizarEndereco(EnderecoForm form) {
		this.setDadosEndereco(form);
		super.atualizarHistorico();
	}

	public void apagarEndereco() {
		super.apagarHistorico();
	}

	public Long getIdCidade() {
		return this.cidade != null ? this.cidade.getId() : null;
	}

	public Long getIdEstado() {
		if (this.getIdCidade() == null)
			return null;
		return this.cidade.getEstado() != null ? this.cidade.getEstado().getId() : null;
	}

	public Long getIdPais() {
		if (this.getIdEstado() == null)
			return null;
		return this.cidade.getEstado().getPais() != null ? this.cidade.getEstado().getPais().getId() : null;
	}

	private void setDadosEndereco(EnderecoForm form) {
		this.setRua(form.rua());
		this.setNumero(form.numero());
		this.setBairro(form.bairro());
		this.setComplemento(form.complemento());
		this.setCodigoPostal(form.codigoPostal());
		this.setCidade(new Cidade(form.idCidade()));
	}
}
