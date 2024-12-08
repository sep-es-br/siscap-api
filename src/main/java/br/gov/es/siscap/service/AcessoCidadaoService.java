package br.gov.es.siscap.service;

import br.gov.es.siscap.client.AcessoCidadaoUserInfoClient;
import br.gov.es.siscap.client.AcessoCidadaoWebClient;
import br.gov.es.siscap.dto.ACUserInfoDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import br.gov.es.siscap.dto.acessocidadaoapi.GrupoACDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

	public List<GrupoACDto> buscarGruposPessoaPorCpf(String cpf) {
		String sub = buscarSubPorCpf(cpf);
		return buscarGruposAgentePublicoPorSub(sub);
	}

	public ACUserInfoDto buscarInformacoesUsuario(String accessToken) {
		return buscarAcessoCidadaoUserInfo(accessToken);
	}

	public List<ACAgentePublicoPapelDto> listarPapeisAgentePublicoPorSub(String sub) {
		return buscarPapeisAgentePublicoPorSub(sub);
	}

	private String buscarSubPorCpf(String cpf) {
		return ACWebClient.buscarSubPorCpf(ACAuthService.getAuthorizationHeader(), cpf).sub();
	}

	private AgentePublicoACDto buscarAgentePublicoPorSub(String sub) {
		return new AgentePublicoACDto(ACWebClient.buscarAgentePublicoPorSub(ACAuthService.getAuthorizationHeader(), sub));
	}

	public List<GrupoACDto> buscarGruposAgentePublicoPorSub(String sub) {
		return ACWebClient.buscarGruposAgentePublicoPorSub(ACAuthService.getAuthorizationHeader(), sub, "Todos");
	}

	private ACUserInfoDto buscarAcessoCidadaoUserInfo(String accessToken) {
		return ACUserInfoClient.buscarUserInfoAcessoCidadao(ACAuthService.getAccessTokenAuthorizationHeader(accessToken));
	}

	/*
	   INTUITO ERA TRAZER A LISTA DE PAPEIS DE UM AGENTE PUBLICO, MAPEAR POR "LotacaoGuid"
	   E PROCURAR ESSAS LOTACOES NO ORGANOGRAMA, TRAZENDO AS ORGANIZACOES

	   IDEIA: CRIAR COLUNA "guid" NAS ORGANIZACOES E VINCULAR COM "LotacaoGuid"
	   |-> PROBLEMA E COMO VINCULAR ORGANIZACAO DO BANCO DO SISCAP COM RETORNO DA API DO ORGANOGRAMA!
	       ex: "SEP" DENTRO DO BANCO E "SEP" DO ORGANOGRAMA -> VINCULAR POR SIGLA? CNPJ?
	            ORG NAO EXISTENTE NO BANCO -> CRIAR NO BANCO? E SE POR ALGUM BUG REPETIR?
	*/

	private List<ACAgentePublicoPapelDto> buscarPapeisAgentePublicoPorSub(String sub) {
		return ACWebClient.buscarPapeisAgentePublicoPorSub(ACAuthService.getAuthorizationHeader(), sub);
	}


}
