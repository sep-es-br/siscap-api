package br.gov.es.siscap.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoAcao;

public interface ProjetoAcaoRepository extends JpaRepository<ProjetoAcao, Long> {

	Set<ProjetoAcao> findAllByProjeto(Projeto projeto);

	@Modifying
    @Query(value = "DELETE FROM projeto_acao WHERE id_projeto = :idProjeto", nativeQuery = true)
    void deleteFisicoPorProjeto(@Param("idProjeto") Long id);

}
