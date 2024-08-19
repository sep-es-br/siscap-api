package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoCidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoCidadeRepository extends JpaRepository<ProjetoCidade, Long> {

	Set<ProjetoCidade> findAllByProjeto(Projeto projeto);

}
