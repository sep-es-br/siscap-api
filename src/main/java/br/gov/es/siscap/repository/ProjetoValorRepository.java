package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoValor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoValorRepository extends JpaRepository<ProjetoValor, Long> {

	Set<ProjetoValor> findAllByProjeto(Projeto projeto);

	ProjetoValor findByProjeto(Projeto projeto);
}
