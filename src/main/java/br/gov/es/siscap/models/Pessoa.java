package br.gov.es.siscap.models;

import br.gov.es.siscap.form.EnderecoForm;
import br.gov.es.siscap.form.PessoaForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "pessoa")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update pessoa set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Pessoa extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "nome_social")
	private String nomeSocial;

	@Column(name = "nacionalidade", nullable = false)
	private String nacionalidade;

	@Column(name = "genero", nullable = false)
	private String genero;

	@Column(name = "cpf")
	private String cpf;

	@Column(name = "email")
	private String email;

	@Column(name = "telefone_comercial")
	private String telefoneComercial;

	@Column(name = "telefone_pessoal")
	private String telefonePessoal;

	@OneToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.REMOVE})
	@SQLJoinTableRestriction("apagado = FALSE")
	@JoinColumn(name = "id_endereco")
	private Endereco endereco;

	@Column(name = "nome_imagem")
	private String nomeImagem;

	@Column(name = "sub")
	private String sub;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "pessoa_area_atuacao",
				joinColumns = {@JoinColumn(name = "id_pessoa")},
				inverseJoinColumns = @JoinColumn(name = "id_area_atuacao", nullable = false))
	private Set<AreaAtuacao> areasAtuacao;

	@OneToMany(mappedBy = "pessoa")
	private Set<PessoaOrganizacao> pessoaOrganizacaoSet;

	@OneToMany(mappedBy = "pessoa")
	private Set<ProjetoPessoa> projetoPessoaSet;

	@OneToMany(mappedBy = "pessoa")
	private Set<ProgramaPessoa> programaPessoaSet;

	public Pessoa(Long id) {
		this.setId(id);
	}

	public Pessoa(PessoaForm form, String nomeImagem) {
		this.setDadosObrigatorios(form);
		this.setDadosOpcionais(form);
		this.criarEndereco(form.endereco());
		this.atualizarImagemPerfil(nomeImagem);
		this.setSub(form.sub());
	}

	public void atualizarPessoa(PessoaForm form) {
		this.setDadosObrigatorios(form);
		this.setDadosOpcionais(form);
		this.atualizarEndereco(form.endereco());
		super.atualizarHistorico();
	}

	public void atualizarImagemPerfil(String nomeImagem) {
		this.setNomeImagem(nomeImagem);
	}

	public void apagarPessoa() {
		this.setCpf(null);
		this.setEmail(null);
		this.setNomeImagem(null);
		this.setSub(null);
		super.apagarHistorico();
	}

	private void setDadosObrigatorios(PessoaForm form) {
		this.setNome(form.nome());
		this.setEmail(form.email());
		this.setNacionalidade(form.nacionalidade());
		this.setGenero(form.genero());
	}

	private void setDadosOpcionais(PessoaForm form) {
		this.setNomeSocial(form.nomeSocial());
		this.setCpf(form.cpf());
		this.setTelefoneComercial(form.telefoneComercial());
		this.setTelefonePessoal(form.telefonePessoal());
		this.setAreasAtuacao(form.idAreasAtuacao() != null ?
					form.idAreasAtuacao().stream().map(AreaAtuacao::new).collect(Collectors.toSet()) : null);
	}

	private void criarEndereco(EnderecoForm enderecoForm) {
		this.setEndereco(enderecoForm != null ? new Endereco(enderecoForm) : null);
	}

	private void atualizarEndereco(EnderecoForm enderecoForm) {
		if (enderecoForm != null) {
			if (this.getEndereco() == null) {
				this.setEndereco(new Endereco(enderecoForm));
			} else {
				this.getEndereco().atualizarEndereco(enderecoForm);
			}
		} else if (this.getEndereco() != null) {
			this.getEndereco().apagarEndereco();
			this.setEndereco(null);
		}
	}
}
