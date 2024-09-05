package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoMicrorregiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoMicrorregiaoRepository extends JpaRepository<ProjetoMicrorregiao, Long> {

	Set<ProjetoMicrorregiao> findAllByProjeto(Projeto projeto);
}
