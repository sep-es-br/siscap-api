package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PessoaOrganizacaoRepository extends JpaRepository<PessoaOrganizacao, Long> {

	Set<PessoaOrganizacao> findAllByPessoa(Pessoa pessoa);

	Set<PessoaOrganizacao> findAllByOrganizacao(Organizacao organizacao);
}
