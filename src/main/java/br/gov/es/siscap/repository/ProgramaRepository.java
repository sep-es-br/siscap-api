package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Programa;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramaRepository extends JpaRepository<Programa, Long> {

	@Query("""
                SELECT p FROM Programa p
                JOIN p.historicoStatus ps
                WHERE ps.inicioEm = (
                    SELECT MAX(hp.inicioEm)
                    FROM ProgramaStatus hp
                    WHERE hp.programa = p
                )
                AND p.apagado = false
                AND (:status IS NULL OR ps.status = :status)
                AND (
                    :search IS NULL OR :search = '' OR
                    LOWER(p.sigla) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    LOWER(p.titulo) LIKE LOWER(CONCAT('%', :search, '%'))
                )
            """)
	Page<Programa> paginarProgramasPorFiltroPesquisaSimples(
			@Param("search") String search, @Param("pageable") Pageable pageable, @Param("status") Integer status);

	@Query("select count(p) from Programa p where year(p.criadoEm) = year(current_date)")
	int contagemAnoAtual();

	@Query("select distinct p from Programa p " +
			"join fetch p.programaAssinantesEdocsSet ap " +
			"join fetch ap.pessoa pe " +
			"where " +
			"p.apagado = false and " +
			"ap.apagado = false and " +
			"p.id = :id ")
	Programa buscarPorIdComAssinantesEPessoa(Long id);
        
        @Query("""
            SELECT p FROM Programa p
            LEFT JOIN FETCH p.historicoStatus
            WHERE p.id = :id
        """)
        Optional<Programa> findByIdWithHistorico(Long id);

}
