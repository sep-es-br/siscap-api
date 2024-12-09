package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.AreaAtuacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaAtuacaoRepository extends JpaRepository<AreaAtuacao, Long> {
}
