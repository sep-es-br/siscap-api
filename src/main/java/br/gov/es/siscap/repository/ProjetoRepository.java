package br.gov.es.siscap.repository;

import br.gov.es.siscap.entity.Projeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoRepository extends JpaRepository<Projeto, Integer> {

    Page<Projeto> findAllByApagadoEmIsNull(Pageable pageable);
}
