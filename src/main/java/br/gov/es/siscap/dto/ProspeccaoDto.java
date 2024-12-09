package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Prospeccao;

import java.util.List;

public record ProspeccaoDto(

			Long id,
			Long idCartaConsulta,
			Long idOrganizacaoProspectora,
			Long idPessoaProspectora,
			Long idOrganizacaoProspectada,
			List<InteressadoDto> interessadosList
) {

	public ProspeccaoDto(Prospeccao prospeccao, List<InteressadoDto> interessadoDtoList) {
		this(
					prospeccao.getId(),
					prospeccao.getCartaConsulta().getId(),
					prospeccao.getOrganizacaoProspectora().getId(),
					prospeccao.getPessoaProspectora().getId(),
					prospeccao.getOrganizacaoProspectada().getId(),
					interessadoDtoList
		);
	}
}