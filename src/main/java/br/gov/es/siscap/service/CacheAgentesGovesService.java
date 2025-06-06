package br.gov.es.siscap.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.es.siscap.dto.opcoes.ResponsavelProponenteOpcoesDto;

@Service
public class CacheAgentesGovesService {

    private List<ResponsavelProponenteOpcoesDto> cache = new ArrayList<>();

    public void carregarCache(List<ResponsavelProponenteOpcoesDto> dados) {
        this.cache = dados;
    }

    public List<ResponsavelProponenteOpcoesDto> getCache() {
        return this.cache;
    }

}
