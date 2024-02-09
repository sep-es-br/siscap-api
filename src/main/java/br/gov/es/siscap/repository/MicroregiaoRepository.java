package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Microregiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MicroregiaoRepository extends JpaRepository<Microregiao, Long> {
}
