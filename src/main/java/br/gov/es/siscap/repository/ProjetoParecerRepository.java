package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoParecer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoParecerRepository extends JpaRepository<ProjetoParecer, Long> {

	Set<ProjetoParecer> findAllByProjeto(Projeto projeto);

    Set<ProjetoParecer> findAllByProjetoId(Long idProjeto);

	@Modifying
    @Query(value = "DELETE FROM projeto_parecer WHERE id_projeto = :idProjeto", nativeQuery = true)
    void deleteFisicoPorProjeto(@Param("idProjeto") Long id);

    boolean existsByProjetoIdAndGuidUnidadeOrganizacao(Long projetoId, String guidUnidadeOrganizacao);

}
