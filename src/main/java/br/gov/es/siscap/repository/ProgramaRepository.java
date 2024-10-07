package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Programa;
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
}
