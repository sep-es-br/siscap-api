package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Prospeccao;
import br.gov.es.siscap.models.ProspeccaoInteressado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProspeccaoInteressadoRepository extends JpaRepository<ProspeccaoInteressado, Long> {

	Set<ProspeccaoInteressado> findAllByProspeccao(Prospeccao prospeccao);
}