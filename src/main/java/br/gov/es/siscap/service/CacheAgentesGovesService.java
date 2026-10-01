package br.gov.es.siscap.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import br.gov.es.siscap.dto.opcoes.ResponsavelProponenteOpcoesDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheAgentesGovesService {

    private List<ResponsavelProponenteOpcoesDto> cache = new ArrayList<>();

    private final PessoaService pessoaService;
	private final Logger logger = LogManager.getLogger(PessoaService.class);

    @PostConstruct
    public void init() {
        logger.debug("Carregar cache com a lista de todos os agentes publicos de GOVES.");
        carregarCache();
    }

    public void carregarCache(List<ResponsavelProponenteOpcoesDto> dados) {
        this.cache = dados;
    }

    public List<ResponsavelProponenteOpcoesDto> getCache() {
        return this.cache;
    }

    public void carregarCache() {
        this.cache = buscarAgentes();
    }

    private List<ResponsavelProponenteOpcoesDto> buscarAgentes() {
        return pessoaService.listarOpcoesDropdownTodosAgentesGoves();
    }

}
