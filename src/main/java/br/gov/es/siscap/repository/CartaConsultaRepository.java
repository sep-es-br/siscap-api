package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.CartaConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartaConsultaRepository extends JpaRepository<CartaConsulta, Long> {
}
