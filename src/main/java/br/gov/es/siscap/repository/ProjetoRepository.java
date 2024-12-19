package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long>, JpaSpecificationExecutor<Projeto> {

	boolean existsBySigla(String sigla);

	Set<Projeto> findAllByPrograma(Programa programa);
}