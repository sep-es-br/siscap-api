package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Endereco;

public record EnderecoDto(
        Long id,
        String rua,
        Integer numero,
        String bairro,
        String complemento,
        String codigoPostal,
        Long idCidade) {

    public EnderecoDto(Endereco endereco) {
        this(endereco.getId(), endereco.getRua(), endereco.getNumero(), endereco.getBairro(), endereco.getComplemento(),
                endereco.getCodigoPostal(), endereco.getCidade().getId());
    }

}
