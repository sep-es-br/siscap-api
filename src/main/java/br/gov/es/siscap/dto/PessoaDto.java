package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Pessoa;

public record PessoaDto(
        Long id,
        String nome,
        String nomeSocial,
        String nacionalidade,
        String genero,
        String cpf,
        EnderecoDto endereco,
        String imagemPerfil) {

    public PessoaDto(Pessoa pessoa) {
        this(pessoa.getId(), pessoa.getNome(), pessoa.getNomeSocial(), pessoa.getNacionalidade(),
                pessoa.getGenero(), pessoa.getCpf(),
                pessoa.getEndereco() != null ? new EnderecoDto(pessoa.getEndereco()) : null, pessoa.getCaminhoImagem());
    }

}
