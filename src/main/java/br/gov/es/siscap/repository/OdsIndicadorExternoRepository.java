package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.OdsIndicadorExterno;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OdsIndicadorExternoRepository extends JpaRepository<OdsIndicadorExterno, Integer> {

    public boolean existsByIdAndIndicadorExternoId( Integer idOdsIndicadorExterno, Integer idIndicadorExterno );

}
