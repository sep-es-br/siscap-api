package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProjetoPessoaRepository extends JpaRepository<ProjetoPessoa, Long> {

    void deleteByIdNotInAndProjetoIs(Collection<Long> id, Projeto projeto);

}
