package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {

    boolean existsByCnpj(String cnpj);

}
