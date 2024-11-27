package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.enums.FormatoDataEnum;
import br.gov.es.siscap.models.Prospeccao;
import br.gov.es.siscap.utils.FormatadorData;

public record ProspeccaoListaDto(

			Long id,
			String nomeOrganizacaoProspectada,
			String tipoOperacao,
			String objetoCartaConsulta,
			String tipoProspeccao,
			String statusProspeccao,
			String dataProspeccao
) {

	public ProspeccaoListaDto(Prospeccao prospeccao) {
		this(
					prospeccao.getId(),
					(prospeccao.getOrganizacaoProspectada().getNomeFantasia() + " - " + prospeccao.getOrganizacaoProspectada().getNome()),
					prospeccao.getCartaConsulta().getTipoOperacao().getTipo(),
					prospeccao.getCartaConsulta().getCartaConsultaObjeto().nome(),
					prospeccao.getTipoProspeccao(),
					prospeccao.getStatusProspeccao(),
					prospeccao.getDataProspeccao() != null ? FormatadorData.formatar(prospeccao.getDataProspeccao(), FormatoDataEnum.SIMPLES) : null
		);
	}
}