package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.TipoEntidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEntidadeRepository extends JpaRepository<TipoEntidade, Long> {
}
