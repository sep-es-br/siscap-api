package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Organizacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {

	@Query("select o from Organizacao o " +
				"where " +
				" o.apagado = false and " +
				" (lower(o.nomeFantasia) like lower(concat('%', :search ,'%')) or " +
				" lower(o.nome) like lower(concat('%', :search , '%')))"
	)
	Page<Organizacao> paginarOrganizacoesPorFiltroPesquisaSimples(String search, Pageable pageable);

	boolean existsByCnpj(String cnpj);
}