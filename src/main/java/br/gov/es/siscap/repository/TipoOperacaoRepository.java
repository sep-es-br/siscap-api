package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.TipoOperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoOperacaoRepository extends JpaRepository<TipoOperacao, Long> {
}
