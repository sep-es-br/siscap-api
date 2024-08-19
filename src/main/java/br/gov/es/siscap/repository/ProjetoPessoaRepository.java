package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoPessoaRepository extends JpaRepository<ProjetoPessoa, Long> {

	Set<ProjetoPessoa> findAllByProjeto(Projeto projeto);

	Set<ProjetoPessoa> findAllByPessoa(Pessoa pessoa);

}
