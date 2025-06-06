package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.DashboardDadosDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 12/02/2025
// ALTERACOES PROVISORIAS APENAS PARA APRESENTACAO; A SEREM REMOVIDAS POSTERIORMENTE

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

	private final ProjetoService projetoService;
	private final ProgramaService programaService;
	private final CartaConsultaService cartaConsultaService;

	public DashboardDadosDto buscarDadosDashboard() {
		return new DashboardDadosDto(
					projetoService.buscarQuantidadeProjetos(),
					projetoService.buscarSomatorioValorEstimadoProjetos(),
					programaService.buscarQuantidadeProgramas(),
					cartaConsultaService.buscarQuantidadeCartasConsulta()
		);
	}
}
