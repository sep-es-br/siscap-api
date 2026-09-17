package br.gov.es.siscap.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import br.gov.es.siscap.models.ProjetoAcao;
import br.gov.es.siscap.models.ProjetoAcaoLocalidadeQuantia;

public interface ProjetoAcaoLocalidadeQuantiaRepository
        extends JpaRepository<ProjetoAcaoLocalidadeQuantia, Long> {

    Set<ProjetoAcaoLocalidadeQuantia> findByProjetoAcao(
            ProjetoAcao projetoAcao);

    List<ProjetoAcaoLocalidadeQuantia> findByProjetoAcaoId(Integer idProjetoAcao);

//     Set<ProjetoAcaoLocalidadeQuantia> findAllByProjetoAcao(ProjetoAcao projetoAcao);

}
