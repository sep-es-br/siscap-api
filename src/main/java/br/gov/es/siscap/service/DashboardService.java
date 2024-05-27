package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.DashboardProjetoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ProjetoService projetoService;

    public DashboardProjetoDto buscarDashboardProjetos() {
        return new DashboardProjetoDto(
                projetoService.buscarQuantidadeProjetos(),
                projetoService.buscarSomatorioValorEstimadoProjetos());
    }

}
