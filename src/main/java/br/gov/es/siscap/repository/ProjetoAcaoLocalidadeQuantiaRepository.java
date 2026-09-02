package br.gov.es.siscap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.es.siscap.models.ProjetoAcao;
import br.gov.es.siscap.models.ProjetoAcaoLocalidadeQuantia;

@Repository
public interface ProjetoAcaoLocalidadeQuantiaRepository
        extends JpaRepository<ProjetoAcaoLocalidadeQuantia, Long> {

    List<ProjetoAcaoLocalidadeQuantia> findByProjetoAcao(
            ProjetoAcao projetoAcao);

    List<ProjetoAcaoLocalidadeQuantia> findByProjetoAcaoId(Integer idProjetoAcao);

}
