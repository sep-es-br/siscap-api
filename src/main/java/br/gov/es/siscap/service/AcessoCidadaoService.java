package br.gov.es.siscap.service;

import br.gov.es.siscap.client.AcessoCidadaoUserInfoClient;
import br.gov.es.siscap.client.AcessoCidadaoWebClient;
import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACUserInfoDto;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcessoCidadaoService {

	private final AcessoCidadaoAutorizacaoService ACAuthService;
	private final AcessoCidadaoWebClient ACWebClient;
	private final AcessoCidadaoUserInfoClient ACUserInfoClient;

	public AgentePublicoACDto buscarPessoaPorCpf(String cpf) {
		String sub = buscarSubPorCpf(cpf);
		return buscarAgentePublicoPorSub(sub);
	}

	public ACUserInfoDto buscarInformacoesUsuario(String accessToken) {
		return buscarAcessoCidadaoUserInfo(accessToken);
	}

	public List<ACAgentePublicoPapelDto> listarPapeisAgentePublicoPorSub(String sub) {
		return buscarPapeisAgentePublicoPorSub(sub);
	}

	public ACAgentePublicoPapelDto buscarGestorNovoConjuntoPorGuidOrganizacao(String guid) {
		return ACWebClient.buscarGestorNovoConjuntoPorGuidOrganizacao(ACAuthService.getAuthorizationHeader(), guid);
	}

	private String buscarSubPorCpf(String cpf) {
		return ACWebClient.buscarSubPorCpf(ACAuthService.getAuthorizationHeader(), cpf).sub();
	}

	private AgentePublicoACDto buscarAgentePublicoPorSub(String sub) {
		return new AgentePublicoACDto(ACWebClient.buscarAgentePublicoPorSub(ACAuthService.getAuthorizationHeader(), sub));
	}

	private ACUserInfoDto buscarAcessoCidadaoUserInfo(String accessToken) {
		LinkedHashMap<String, Object> userInfoObj = ACUserInfoClient.buscarUserInfoAcessoCidadao(ACAuthService.getAccessTokenAuthorizationHeader(accessToken));
		return new ACUserInfoDto(userInfoObj);
	}

	private List<ACAgentePublicoPapelDto> buscarPapeisAgentePublicoPorSub(String sub) {
		return ACWebClient.buscarPapeisAgentePublicoPorSub(ACAuthService.getAuthorizationHeader(), sub);
	}
}
