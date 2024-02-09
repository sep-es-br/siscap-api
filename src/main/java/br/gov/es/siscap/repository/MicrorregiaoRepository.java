package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Microrregiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MicrorregiaoRepository extends JpaRepository<Microrregiao, Long> {
}
