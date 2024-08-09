package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.AreaAtuacao;
import br.gov.es.siscap.models.Pessoa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class NewPessoaDto {

	private Long id;
	private String nome;
	private String nomeSocial;
	private String nacionalidade;
	private String genero;
	private String cpf;
	private String email;
	private String telefoneComercial;
	private String telefonePessoal;
	private EnderecoDto endereco;
	private Set<Long> idAreasAtuacao;
	private byte[] imagemPerfil;
	private Long idOrganizacao;
	private Boolean isResponsavelOrganizacao;

	// Ver se vale a pena construir novo DTO só com info de pessoa
	// Usar setters dentro do serviço para outras informações
	// |-> Endereço, AreasAtuacao, imagemPerfil, PessoaOrganizacao

	public NewPessoaDto(Pessoa pessoa) {
		this.setId(pessoa.getId());
		this.setNome(pessoa.getNome());
		this.setNomeSocial(pessoa.getNomeSocial());
		this.setNacionalidade(pessoa.getNacionalidade());
		this.setGenero(pessoa.getGenero());
		this.setCpf(pessoa.getCpf());
		this.setEmail(pessoa.getEmail());
		this.setTelefoneComercial(pessoa.getTelefoneComercial());
		this.setTelefonePessoal(pessoa.getTelefonePessoal());
		this.setEndereco(pessoa.getEndereco() != null ? new EnderecoDto(pessoa.getEndereco()) : null);
		this.setIdAreasAtuacao(pessoa.getAreasAtuacao() != null ? pessoa.getAreasAtuacao().stream().map(AreaAtuacao::getId).collect(Collectors.toSet()) : null);
	}
}
