package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.LocalidadeQuantia;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import java.math.BigDecimal;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalidadeQuantiaRepository extends JpaRepository<LocalidadeQuantia, Long> {

	Set<LocalidadeQuantia> findAllByProjeto(Projeto projeto);
        
	@Query("""
                    select lq from LocalidadeQuantia lq
                    join lq.projeto pj
                    join pj.programaHistorico pp
                    join pp.programa pg
                    where lq.apagado = false
                      and pj.apagado = false
                      and pg.apagado = false
                      and pp.apagadoEm is null
                      and pg = :programa
                """)
	Set<LocalidadeQuantia> buscarPorPrograma(Programa programa);

	@Query("select sum(lq.quantia) from LocalidadeQuantia lq where lq.apagado = false")
	BigDecimal somarValorEstimadoTodosProjetos();

	@Modifying
    @Query(value = "DELETE FROM localidade_quantia WHERE id_projeto = :idProjeto", nativeQuery = true)
    void deleteFisicoPorProjeto(@Param("idProjeto") Long id);

}