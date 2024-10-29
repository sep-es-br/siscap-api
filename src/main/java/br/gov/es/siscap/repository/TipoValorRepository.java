package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.TipoValor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoValorRepository extends JpaRepository<TipoValor, Long> {
}