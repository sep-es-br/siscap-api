package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaOrganizacao;
import br.gov.es.siscap.models.ProgramaOrganizacaoId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProgramaOrganizacaoRepository extends JpaRepository<ProgramaOrganizacao, ProgramaOrganizacaoId> {

	Set<ProgramaOrganizacao> findAllByPrograma(Programa programa);

}
