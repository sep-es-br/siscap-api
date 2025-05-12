package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoIndicador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoIndicadorRepository extends JpaRepository<ProjetoIndicador, Long> {

	Set<ProjetoIndicador> findAllByProjeto(Projeto projeto);

}
