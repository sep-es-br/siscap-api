package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoCamposComplementacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoComplementosRepository extends JpaRepository<ProjetoCamposComplementacao, Long> {

	Set<ProjetoCamposComplementacao> findAllByProjeto(Projeto projeto);

}
