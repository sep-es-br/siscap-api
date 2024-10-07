package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaValor;
import br.gov.es.siscap.models.Valor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProgramaValorRepository extends JpaRepository<ProgramaValor, Long> {

	Set<ProgramaValor> findAllByPrograma(Programa programa);

	ProgramaValor findByPrograma(Programa programa);
}
