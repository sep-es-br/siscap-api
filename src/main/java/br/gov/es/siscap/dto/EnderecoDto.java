package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Endereco;

public record EnderecoDto(

			Long id,
			String rua,
			String numero,
			String bairro,
			String complemento,
			String codigoPostal,
			Long idCidade,
			Long idEstado,
			Long idPais
) {

	public EnderecoDto(Endereco endereco) {
		this(
					endereco.getId(),
					endereco.getRua(),
					endereco.getNumero(),
					endereco.getBairro(),
					endereco.getComplemento(),
					endereco.getCodigoPostal(),
					endereco.getIdCidade(),
					endereco.getIdEstado(),
					endereco.getIdPais()
		);
	}
}
