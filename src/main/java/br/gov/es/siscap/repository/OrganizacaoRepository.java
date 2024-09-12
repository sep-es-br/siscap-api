package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Organizacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {

	Page<Organizacao> findAllByNomeFantasiaContainingIgnoreCaseOrNomeContainingIgnoreCase(String nomeFantasia, String nome, Pageable pageable);

	boolean existsByCnpj(String cnpj);
}