package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaOrganizacaoDto;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaUnidadeInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TesteUsuarioRolesService {

	private final AcessoCidadaoService acessoCidadaoService;
	private final OrganogramaService organogramaService;

	public List<ACAgentePublicoPapelDto> listarPapeisAgentePublicoPorSub(String sub) {
		return acessoCidadaoService.listarPapeisAgentePublicoPorSub(sub);
	}

	public OrganogramaUnidadeInfoDto listarUnidadeInfoPorLotacaoGuid(String lotacaoGuid) {
		return organogramaService.listarUnidadeInfoPorLotacaoGuid(lotacaoGuid);
	}

	public OrganogramaOrganizacaoDto listarDadosOrganizacaoPorGuid(String guid) {
		return organogramaService.listarDadosOrganizacaoPorGuid(guid);
	}
}
