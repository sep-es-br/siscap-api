package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    Optional<Pessoa> findBySubNovo(String subNovo);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

}
