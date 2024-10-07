package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

	@Query("select p from Projeto p " +
				"where " +
				"p.status.id = 1 and " +
				"p.apagado = false and " +
				"(lower(p.sigla) like lower(concat('%', :search ,'%')) or " +
				"lower(p.titulo) like lower(concat('%', :search , '%')))"
	)
	Page<Projeto> paginarProjetosPorFiltroPesquisaSimples(String search, Pageable pageable);

	boolean existsBySigla(String sigla);

	@Query("select sum(pv.quantia) from Projeto p " +
				" inner join ProjetoValor pv on pv.projeto = p " +
				" where pv.apagado = false"
	)
	BigDecimal somarValorEstimadoTodosProjetos();

}
