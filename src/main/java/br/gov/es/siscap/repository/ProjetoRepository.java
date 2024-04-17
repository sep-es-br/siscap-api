package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    boolean existsBySigla(String sigla);

}
