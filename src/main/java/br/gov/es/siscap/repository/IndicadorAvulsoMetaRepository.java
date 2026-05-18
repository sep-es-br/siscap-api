package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.IndicadorAvulsoMeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicadorAvulsoMetaRepository extends JpaRepository<IndicadorAvulsoMeta, Integer> {
    void deleteByIndicadorAvulsoId(Integer idIndicadorAvulso);
}
