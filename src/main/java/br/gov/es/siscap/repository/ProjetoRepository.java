package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {


	String FILTRO_SIGLA = "lower(p.sigla) like lower(concat('%', :sigla ,'%'))";
	String FILTRO_TITULO = "lower(p.titulo) like lower(concat('%', :titulo , '%'))";
	String FILTRO_ORGANIZACAO = "p.organizacao.id = :idOrganizacao";
	String FILTRO_STATUS = "p.status = :status";
	String FILTRO_DATA = "p.criadoEm between :inicio and :fim";

	@Query("select p from Projeto p " +
				"where " +
				"p.tipoStatus.id = 1 and " +
				"p.apagado = false and " +
				"(lower(p.sigla) like lower(concat('%', :search ,'%')) or " +
				"lower(p.titulo) like lower(concat('%', :search , '%')))"
	)
	Page<Projeto> paginarProjetosPorFiltroPesquisaSimples(String search, Pageable pageable);


	@Query("select p from Projeto p where " +
				"(" +
				FILTRO_SIGLA + " or " +
				FILTRO_TITULO + " or " +
				FILTRO_ORGANIZACAO + " or " +
				FILTRO_STATUS + " or " +
				FILTRO_DATA + " " +
				")")
	Page<Projeto> paginarProjetosPorFiltroPesquisaAvancada(
				Pageable pageable,
				String sigla,
				String titulo,
				Long idOrganizacao,
				String status,
				LocalDateTime inicio,
				LocalDateTime fim);

	boolean existsBySigla(String sigla);

	Set<Projeto> findAllByPrograma(Programa programa);
}