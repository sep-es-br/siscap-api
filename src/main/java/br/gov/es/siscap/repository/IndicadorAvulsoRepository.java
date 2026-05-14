package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.IndicadorAvulso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicadorAvulsoRepository extends JpaRepository<IndicadorAvulso, Integer> {

}
