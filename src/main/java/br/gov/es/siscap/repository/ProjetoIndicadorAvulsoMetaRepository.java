package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.ProjetoIndicadorAvulsoMeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetoIndicadorAvulsoMetaRepository extends JpaRepository<ProjetoIndicadorAvulsoMeta, Integer> {

	void deleteByProjetoIndicadorAvulsoId(Integer idProjetoIndicadorAvulso);

}
