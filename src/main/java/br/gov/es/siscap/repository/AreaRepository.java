package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Area;
import br.gov.es.siscap.models.Eixo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findAllByEixo(Eixo eixo);

}
