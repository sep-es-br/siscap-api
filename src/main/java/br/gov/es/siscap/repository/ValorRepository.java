package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Valor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorRepository extends JpaRepository<Valor, Long> {
}
