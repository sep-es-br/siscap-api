package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Prospeccao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProspeccaoRepository extends JpaRepository<Prospeccao, Long> {

	@Query("select p from Prospeccao p " +
				"where p.apagado = false and " +
				"(lower(p.organizacaoProspectada.nomeFantasia) like lower(concat('%', :search, '%')) or " +
				"lower(p.organizacaoProspectada.nome) like lower(concat('%', :search, '%')))")
	Page<Prospeccao> paginarProspeccoesPorFiltroPesquisaSimples(String search, Pageable pageable);

	@Query("select count(p) from Prospeccao p where year(p.criadoEm) = year(current_date)")
	int contagemAnoAtual();
}