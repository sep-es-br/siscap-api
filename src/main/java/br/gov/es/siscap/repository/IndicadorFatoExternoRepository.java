package br.gov.es.siscap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.gov.es.siscap.models.IndicadorFatoExterno;

public interface IndicadorFatoExternoRepository extends JpaRepository<IndicadorFatoExterno, Long> {

        @Query("""
                SELECT DISTINCT f.indicador
                FROM IndicadorFatoExterno f
                JOIN f.gestao g
                LEFT JOIN g.labels gl
                WHERE (:idGestao IS NULL OR g.id = :idGestao)
                AND (:desafios IS NULL OR f.desafio.id IN :desafios)
                AND (:labels IS NULL OR gl.label.id IN :labels)
        """)
        List<IndicadorExterno> buscarPorFiltros(
                        @Param("idGestao") Long idGestao,
                        @Param("labels") List<Long> labels,
                        @Param("desafios") List<Long> desafios);

        @Query("""
                SELECT f
                FROM FatoIndicador f
                JOIN FETCH f.indicador i
                WHERE i.id IN :ids
        """)
        List<IndicadorFatoExterno> findByIndicadorIds(@Param("ids") List<Integer> ids);

}
