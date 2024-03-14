package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Eixo;
import br.gov.es.siscap.models.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EixoRepository extends JpaRepository<Eixo, Long> {

    List<Eixo> findAllByPlano(Plano plano);

}
