package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.TipoOrganizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoOrganizacaoRepository extends JpaRepository<TipoOrganizacao, Long> {
}
