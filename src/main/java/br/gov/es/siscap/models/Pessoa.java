package br.gov.es.siscap.models;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;

import br.gov.es.siscap.form.PessoaForm;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	private long id;

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

	@OneToOne(cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.REMOVE })
	@SQLJoinTableRestriction("apagado = FALSE")
	@JoinColumn(name = "id_endereco")
	private Endereco endereco;

	@Column(name = "nome_imagem")
	private String nomeImagem;

	@Column(name = "sub")
	private String sub;

	@ManyToMany()
	@JoinTable(name = "pessoa_area_atuacao", joinColumns = {
			@JoinColumn(name = "id_pessoa") }, inverseJoinColumns = @JoinColumn(name = "id_area_atuacao", nullable = false))
	private Set<AreaAtuacao> areasAtuacao;

	@OneToMany(mappedBy = "pessoa")
	private Set<PessoaOrganizacao> pessoaOrganizacaoSet;

	@OneToMany(mappedBy = "pessoa")
	private Set<ProjetoPessoa> projetoPessoaSet;

	@OneToMany(mappedBy = "pessoa")
	private Set<ProgramaPessoa> programaPessoaSet;

	/*
	 * so para garantir que o nome da pessoa seja sempre retornado em maiúsculo,
	 * mesmo que esteja salvo em minúsculo no banco de dados.
	 * Isso é útil para padronizar a exibição do nome em diferentes partes do
	 * sistema, evitando inconsistências visuais.
	 */
	public String getNome() {
		return nome == null ? null : nome.toUpperCase(Locale.ROOT);
	}

	/* so para garantir que o nome da pessoa seja sempre salvo em maiúsculo */
	public void setNome(String nome) {
		this.nome = nome == null ? null : nome.toUpperCase(Locale.ROOT);
	}

	public Pessoa(Long id) {
		this.setId(id);
	}

	public Pessoa(PessoaForm form, String nomeImagem) {
		
		this.setCpf(null);
		this.setGenero(null);
		this.setEndereco(null);

		this.setDadosObrigatorios(form);
		this.setDadosOpcionais(form);
		this.atualizarImagemPerfil(nomeImagem);
		this.setSub(form.sub());

	}

	public void atualizarPessoa(PessoaForm form) {
		this.setDadosObrigatorios(form);
		this.setDadosOpcionais(form);
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
	}

	private void setDadosOpcionais(PessoaForm form) {
		this.setNomeSocial(form.nomeSocial());
		this.setTelefoneComercial(form.telefoneComercial());
		this.setTelefonePessoal(form.telefonePessoal());
		this.setAreasAtuacao(form.idAreasAtuacao() != null
				? form.idAreasAtuacao().stream().map(AreaAtuacao::new).collect(Collectors.toSet())
				: null);
	}

}
