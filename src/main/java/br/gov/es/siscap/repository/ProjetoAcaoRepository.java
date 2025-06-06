package br.gov.es.siscap.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoAcao;

public interface ProjetoAcaoRepository extends JpaRepository<ProjetoAcao, Long> {

	Set<ProjetoAcao> findAllByProjeto(Projeto projeto);

}
