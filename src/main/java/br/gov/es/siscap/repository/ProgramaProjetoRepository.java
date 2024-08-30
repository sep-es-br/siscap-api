package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaProjeto;
import br.gov.es.siscap.models.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProgramaProjetoRepository extends JpaRepository<ProgramaProjeto, Long> {

	Set<ProgramaProjeto> findAllByPrograma(Programa programa);

	Set<ProgramaProjeto> findAllByProjeto(Projeto projeto);
}
