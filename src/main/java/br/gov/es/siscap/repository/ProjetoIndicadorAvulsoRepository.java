package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoIndicadorAvulso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoIndicadorAvulsoRepository extends JpaRepository<ProjetoIndicadorAvulso, Integer> {

	Set<ProjetoIndicadorAvulso> findAllByProjeto(Projeto projeto);

}
