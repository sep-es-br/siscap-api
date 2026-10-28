package br.gov.es.siscap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.siscap.models.ModeloOrientacaoAssinaturaPrograma;
import br.gov.es.siscap.repository.ModeloOrientacaoAssinaturaProgramaRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModeloOrientacaoAssinaturaProgramaService {

        private final ModeloOrientacaoAssinaturaProgramaRepository repository;

        @Transactional(readOnly = true)
        public ModeloOrientacaoAssinaturaPrograma buscarModeloAtivoPorGuidLotacao(
                        String guidLotacao) {

                return repository.findByGuidLotacaoIgnoreCaseAndAtivoTrueAndApagadoFalse(guidLotacao)
                                .orElseGet(ModeloOrientacaoAssinaturaPrograma::new);
        }

}