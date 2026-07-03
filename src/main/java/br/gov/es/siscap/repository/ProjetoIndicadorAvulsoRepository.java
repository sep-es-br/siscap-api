package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoIndicadorAvulso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoIndicadorAvulsoRepository extends JpaRepository<ProjetoIndicadorAvulso, Integer> {

	Set<ProjetoIndicadorAvulso> findAllByProjeto(Projeto projeto);

	@Modifying
    @Query(value = "DELETE FROM projeto_indicador_avulso WHERE id_projeto = :idProjeto", nativeQuery = true)
    void deleteFisicoPorProjeto(@Param("idProjeto") Long id);

}
