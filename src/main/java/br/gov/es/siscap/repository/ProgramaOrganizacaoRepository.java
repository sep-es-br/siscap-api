package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaOrganizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProgramaOrganizacaoRepository extends JpaRepository<ProgramaOrganizacao, Long> {

	Set<ProgramaOrganizacao> findAllByPrograma(Programa programa);

	Set<ProgramaOrganizacao> findAllByPessoa(Pessoa pessoa);
}
