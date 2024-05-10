package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Estado;
import br.gov.es.siscap.models.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {

    List<Estado> findAllByPaisOrderByNome(Pais pais);

}
