package br.gov.es.siscap.service;

import br.gov.es.siscap.client.OrganogramaWebClient;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaOrganizacaoDto;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaOrganizacaoInfoEssencialDto;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaUnidadeInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganogramaService {

	@Value("${guidGOVES}")
	private String GUID_GOVES;

	private final AcessoCidadaoAutorizacaoService ACAuthService;
	private final OrganogramaWebClient OrganogramaWebClient;

	public OrganogramaOrganizacaoDto listarDadosOrganizacaoPorGuid(String guid) {
		return buscarOrganizacaoPorGuid(guid);
	}

	public OrganogramaUnidadeInfoDto listarUnidadeInfoPorLotacaoGuid(String lotacaoGuid) {
		return buscarUnidadeInfoPorGuid(lotacaoGuid);
	}

	public List<OrganogramaOrganizacaoInfoEssencialDto> listarOrganizacoesFilhasGOVES() {
		return OrganogramaWebClient.buscarOrganizacoesFilhasGOVES(ACAuthService.getAuthorizationHeader(), GUID_GOVES)
					.stream()
					.filter(this::validarOrganizacaoAPIOrganograma).toList();
	}

	private OrganogramaOrganizacaoDto buscarOrganizacaoPorGuid(String guid) {
		return OrganogramaWebClient.buscarOrganizacaoPorGuid(ACAuthService.getAuthorizationHeader(), guid);
	}

	private OrganogramaUnidadeInfoDto buscarUnidadeInfoPorGuid(String guid) {
		return OrganogramaWebClient.buscarUnidadeInfoPorGuid(ACAuthService.getAuthorizationHeader(), guid);
	}

	private boolean validarOrganizacaoAPIOrganograma(OrganogramaOrganizacaoInfoEssencialDto organogramaOrganizacaoInfoEssencialDto) {
		return
					organogramaOrganizacaoInfoEssencialDto.cnpj() != null &&
								!organogramaOrganizacaoInfoEssencialDto.cnpj().isEmpty() &&
								!organogramaOrganizacaoInfoEssencialDto.cnpj().equals("00000000000000");
	}
}