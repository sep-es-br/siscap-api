package br.gov.es.siscap.models;

import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.form.PessoaFormUpdate;
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
	private LocalDateTime criadoEm;

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@Column(name = "apagado")
	private boolean apagado = Boolean.FALSE;

	public Pessoa(Long id) {
		this.id = id;
	}

	public Pessoa(PessoaForm form, String nomeImagem) {
		this.nome = form.nome();
		this.nomeSocial = form.nomeSocial();
		this.nacionalidade = form.nacionalidade();
		this.genero = form.genero();
		this.cpf = form.cpf();
		this.email = form.email();
		this.telefoneComercial = form.telefoneComercial();
		this.telefonePessoal = form.telefonePessoal();
		this.endereco = form.endereco() != null ? new Endereco(form.endereco()) : null;
		this.sub = form.sub();
		this.areasAtuacao = form.idAreasAtuacao() != null ?
					form.idAreasAtuacao().stream().map(AreaAtuacao::new).collect(Collectors.toSet()) : null;
		this.nomeImagem = nomeImagem;
		this.criadoEm = LocalDateTime.now();
	}

	public void atualizar(PessoaFormUpdate form) {
		this.nome = form.nome();
		this.nomeSocial = form.nomeSocial();
		this.nacionalidade = form.nacionalidade();
		this.genero = form.genero();
		this.cpf = form.cpf();
		this.email = form.email();
		this.telefoneComercial = form.telefoneComercial();
		this.telefonePessoal = form.telefonePessoal();
		if (form.endereco() == null)
			this.endereco = null;
		else if (this.endereco != null)
			this.endereco.atualizarEndereco(form.endereco());
		else
			this.endereco = new Endereco(form.endereco());
		this.areasAtuacao = form.idAreasAtuacao().stream().map(AreaAtuacao::new).collect(Collectors.toSet());
		this.setAtualizadoEm(LocalDateTime.now());
	}

	public void atualizarImagemPerfil(String nomeImagem) {
		this.nomeImagem = nomeImagem;
	}

	public void apagar() {
		this.cpf = null;
		this.email = null;
		this.nomeImagem = null;
		this.sub = null;
		this.atualizadoEm = LocalDateTime.now();
	}
}
