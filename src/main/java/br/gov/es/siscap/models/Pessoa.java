package br.gov.es.siscap.models;

import br.gov.es.siscap.form.PessoaForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "pessoa")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update pessoa set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Pessoa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome")
	private String nome;

	@Column(name = "nome_social")
	private String nomeSocial;

	@Column(name = "nacionalidade")
	private String nacionalidade;

	@Column(name = "genero")
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
				inverseJoinColumns = @JoinColumn(name = "id_area_atuacao"))
	private Set<AreaAtuacao> areasAtuacao;

	@OneToMany(mappedBy = "pessoa")
	private Set<PessoaOrganizacao> pessoaOrganizacaoSet;

	@OneToMany(mappedBy = "pessoa")
	private Set<ProjetoPessoa> projetoPessoaSet;

	@OneToMany(mappedBy = "pessoa")
	private Set<ProgramaPessoa> programaPessoaSet;

	@DateTimeFormat
	@Column(name = "criado_em")
	private LocalDateTime criadoEm = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@Column(name = "apagado")
	private boolean apagado = Boolean.FALSE;

	public Pessoa(Long id) {
		this.id = id;
	}

	public Pessoa(PessoaForm form, String nomeImagem) {
		setDadosObrigatorios(form);
		setDadosOpcionais(form);
		this.atualizarImagemPerfil(nomeImagem);
		this.setSub(form.sub());
	}

	public void atualizarPessoa(PessoaForm form) {
		setDadosObrigatorios(form);
		setDadosOpcionais(form);
		this.setAtualizadoEm(LocalDateTime.now());
	}

	public void atualizarImagemPerfil(String nomeImagem) {
		this.setNomeImagem(nomeImagem);
	}

	public void apagarPessoa() {
		this.cpf = null;
		this.email = null;
		this.nomeImagem = null;
		this.sub = null;
		this.atualizadoEm = LocalDateTime.now();
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
		this.setEndereco(form.endereco() != null ? new Endereco(form.endereco()) : null);
		this.setAreasAtuacao(form.idAreasAtuacao() != null ?
					form.idAreasAtuacao().stream().map(AreaAtuacao::new).collect(Collectors.toSet()) : null);
	}
}
