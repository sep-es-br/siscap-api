package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaAssinaturaEdocs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProgramaAssinaturaEdocsRepository extends JpaRepository<ProgramaAssinaturaEdocs, Long> {

	Set<ProgramaAssinaturaEdocs> findAllByPrograma(Programa programa);

	Set<ProgramaAssinaturaEdocs> findAllByPessoa(Pessoa pessoa);
}
