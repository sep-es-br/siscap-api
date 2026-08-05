package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoPlanejamentoPpaLoa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Set;

public interface ProjetoPlanejamentoPpaLoaRepository extends JpaRepository<ProjetoPlanejamentoPpaLoa, Long> {

	Set<ProjetoPlanejamentoPpaLoa> findAllByProjeto(Projeto projeto);

	@Modifying
    @Query(value = "DELETE FROM projeto_planejamento_ppa_loa WHERE id_projeto = :idProjeto", nativeQuery = true)
    void deleteFisicoPorProjeto(@Param("idProjeto") Long id);

}
