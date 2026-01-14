package br.gov.es.siscap.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.gov.es.siscap.models.TipoMotivoArquivamento;

@Repository
public interface TipoMotivoArquivamentoRepository extends JpaRepository<TipoMotivoArquivamento, Long> {
    Optional<TipoMotivoArquivamento> findByCodigo(String codigo);
}