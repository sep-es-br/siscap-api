package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.ProjetoIndicadorAvulsoMeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetoIndicadorAvulsoMetaRepository extends JpaRepository<ProjetoIndicadorAvulsoMeta, Integer> {

	void deleteByProjetoIndicadorAvulsoId(Integer idProjetoIndicadorAvulso);

    @Modifying
    @Query(value = """
    DELETE FROM projeto_indicador_avulso_meta m
    USING projeto_indicador_avulso a
    WHERE m.id_projeto_indicador_avulso = a.id
      AND a.id_projeto = :idProjeto
    """, nativeQuery = true)
    void deleteFisicoPorProjeto(@Param("idProjeto") Long id);


}
