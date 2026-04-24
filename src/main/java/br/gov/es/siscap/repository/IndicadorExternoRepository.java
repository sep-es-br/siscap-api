package br.gov.es.siscap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.gov.es.siscap.models.IndicadorExterno;

public interface IndicadorExternoRepository extends JpaRepository<IndicadorExterno, Long> {

    @Query("""
                SELECT DISTINCT ie
                FROM IndicadorExterno ie

                JOIN IndicadorGestao ig ON ig.indicador.id = ie.id
                JOIN ig.gestao g

                LEFT JOIN IndicadorGestaoLabel igl ON igl.indicadorGestao.id = ig.id
                LEFT JOIN igl.label l
                LEFT JOIN igl.valorLabel lv

                LEFT JOIN ig.desafio d

                WHERE g.id = :gestaoId

                AND (:labels IS NULL OR l.id IN :labels)
                AND (:valores IS NULL OR lv.id IN :valores)
                AND (:desafios IS NULL OR d.id IN :desafios)
            """)
    List<IndicadorExterno> buscarPorFiltros(
            @Param("gestaoId") Long gestaoId,
            @Param("labels") List<Long> labels,
            @Param("valores") List<Long> valores,
            @Param("desafios") List<Long> desafios);

}
