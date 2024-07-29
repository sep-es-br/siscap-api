package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    boolean existsBySigla(String sigla);

    @Query("select sum(p.valorEstimado) from Projeto p ")
    BigDecimal somarValorEstimadoTodosProjetos();

}
