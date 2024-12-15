package br.gov.es.siscap.service;

import br.gov.es.siscap.client.OrganogramaWebClient;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaOrganizacaoDto;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaUnidadeInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganogramaService {

	private final AcessoCidadaoAutorizacaoService ACAuthService;
	private final OrganogramaWebClient OrganogramaWebClient;

	public OrganogramaOrganizacaoDto listarDadosOrganizacaoPorGuid(String guid) {
		return buscarOrganizacaoPorGuid(guid);
	}

	public OrganogramaUnidadeInfoDto listarUnidadeInfoPorLotacaoGuid(String lotacaoGuid) {
		return buscarUnidadeInfoPorGuid(lotacaoGuid);
	}

	private OrganogramaOrganizacaoDto buscarOrganizacaoPorGuid(String guid) {
		return OrganogramaWebClient.buscarOrganizacaoPorGuid(ACAuthService.getAuthorizationHeader(), guid);
	}

	private OrganogramaUnidadeInfoDto buscarUnidadeInfoPorGuid(String guid) {
		return OrganogramaWebClient.buscarUnidadeInfoPorGuid(ACAuthService.getAuthorizationHeader(), guid);
	}
}