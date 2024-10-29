package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

	@Query("select p from Projeto p " +
				"where " +
				"p.tipoStatus.id = 1 and " +
				"p.apagado = false and " +
				"(lower(p.sigla) like lower(concat('%', :search ,'%')) or " +
				"lower(p.titulo) like lower(concat('%', :search , '%')))"
	)
	Page<Projeto> paginarProjetosPorFiltroPesquisaSimples(String search, Pageable pageable);

	boolean existsBySigla(String sigla);

	Set<Projeto> findAllByPrograma(Programa programa);
}