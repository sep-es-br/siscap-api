package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProgramaPessoaRepository extends JpaRepository<ProgramaPessoa, Long> {

	Set<ProgramaPessoa> findAllByPrograma(Programa programa);

	Set<ProgramaPessoa> findAllByPessoa(Pessoa pessoa);
}
