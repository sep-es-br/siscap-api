package br.gov.es.siscap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.gov.es.siscap.models.IndicadorGestaoExterno;

public interface IndicadorGestaoExternoRepository extends JpaRepository<IndicadorGestaoExterno, Long> {

    /**
     * Busca todas as gestões ativas com seus labels e valores já carregados,
     * evitando N+1 com JOIN FETCH.
     */
    @Query("""
            SELECT DISTINCT g FROM IndicadorGestaoExterno g
            LEFT JOIN FETCH g.labels l
            LEFT JOIN FETCH l.valores
            WHERE g.ativa = true
            AND (g.apagado = false OR g.apagado IS NULL)
            ORDER BY g.nome
            """)
    List<IndicadorGestaoExterno> findAllAtivasComLabels();

}
