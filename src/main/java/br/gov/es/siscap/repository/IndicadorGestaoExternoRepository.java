package br.gov.es.siscap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.gov.es.siscap.models.IndicadorGestaoExterno;

public interface IndicadorGestaoExternoRepository extends JpaRepository<IndicadorGestaoExterno, Long> {

    @Query("""
            SELECT DISTINCT g FROM Gestao g
            LEFT JOIN FETCH g.labels gl
            LEFT JOIN FETCH gl.label l
            WHERE g.ativa = true
            ORDER BY g.nome
        """)
    List<IndicadorGestaoExterno> findAllAtivasComLabels();

}
