package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.TipoPapel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPapelRepository extends JpaRepository<TipoPapel, Long> {
}