package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Programa;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramaRepository extends JpaRepository<Programa, Long> {

	@Query("select p from Programa p " +
			"where " +
			"p.apagado = false and " +
			"(lower(p.sigla) like lower(concat('%', :search ,'%')) or " +
			"lower(p.titulo) like lower(concat('%', :search , '%')))")
	Page<Programa> paginarProgramasPorFiltroPesquisaSimples(String search, Pageable pageable);

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
