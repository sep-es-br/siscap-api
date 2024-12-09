package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.LocalidadeQuantia;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Set;

@Repository
public interface LocalidadeQuantiaRepository extends JpaRepository<LocalidadeQuantia, Long> {

	Set<LocalidadeQuantia> findAllByProjeto(Projeto projeto);

	@Query("select lq from LocalidadeQuantia lq " +
				"inner join Projeto pj on lq.projeto = pj " +
				"inner join Programa pg on pj.programa = pg " +
				"where lq.apagado = false and pj.apagado = false and pg.apagado = false and pg = :programa"
	)
	Set<LocalidadeQuantia> buscarPorPrograma(Programa programa);

	@Query("select sum(lq.quantia) from LocalidadeQuantia lq where lq.apagado = false")
	BigDecimal somarValorEstimadoTodosProjetos();
}