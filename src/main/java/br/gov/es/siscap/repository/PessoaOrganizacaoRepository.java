package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PessoaOrganizacaoRepository extends JpaRepository<PessoaOrganizacao, Long> {

	Set<PessoaOrganizacao> findAllByPessoa(Pessoa pessoa);

	Set<PessoaOrganizacao> findAllByOrganizacao(Organizacao organizacao);

	@Query("select po from PessoaOrganizacao po where po.pessoa.id = ?1")
	Set<PessoaOrganizacao> findAllByIdPessoa(Long idPessoa);

	@Query("select po from PessoaOrganizacao po where po.organizacao.id = ?1")
	Set<PessoaOrganizacao> findByAllByIdOrganizacao(Long idOrganizacao);
}
