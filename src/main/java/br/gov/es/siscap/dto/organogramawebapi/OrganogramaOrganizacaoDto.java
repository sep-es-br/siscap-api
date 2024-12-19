package br.gov.es.siscap.dto.organogramawebapi;

public record OrganogramaOrganizacaoDto(

			String guid,
			String cnpj,
			String razaoSocial,
			String nomeFantasia,
			String sigla,
			int idEsfera,
			int idPoder,
			int idTipoOrganizacao,
			int idOrganizacaoPai
) {
}