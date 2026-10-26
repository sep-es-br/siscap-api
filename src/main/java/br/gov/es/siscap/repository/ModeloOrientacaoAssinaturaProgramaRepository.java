package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.ModeloOrientacaoAssinaturaPrograma;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloOrientacaoAssinaturaProgramaRepository
        extends JpaRepository<ModeloOrientacaoAssinaturaPrograma, Long> {

    Optional<ModeloOrientacaoAssinaturaPrograma> findByGuidLotacaoAndAtivoTrue(String guidLotacao);

}
