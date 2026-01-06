package br.gov.es.siscap.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.es.siscap.models.CartaConsulta;
import br.gov.es.siscap.models.CartaConsultaDestinatario;

@Repository
public interface CartaConsultaDestinatariosRepository extends JpaRepository<CartaConsultaDestinatario, Long> {

    Set<CartaConsultaDestinatario> findAllByCartaConsulta(CartaConsulta cartaConsulta);
}