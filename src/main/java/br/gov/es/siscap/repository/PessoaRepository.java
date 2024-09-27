package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

	@Query("select p from Pessoa p " +
				"left join PessoaOrganizacao po on po.pessoa = p " +
				"left join Organizacao o on o = po.organizacao " +
				"where " +
				" p.apagado = false and " +
				" (po is null or po.apagado = false) and " +
				" (o is null or o.apagado = false) and " +
				" (lower(p.nome) like lower(concat('%', :search, '%')) or " +
				" lower(p.email) like lower(concat('%', :search, '%')) or " +
				" lower(o.nome) like lower(concat('%', :search, '%')) or " +
				" lower(o.nomeFantasia) like lower(concat('%', :search, '%')))"
	)
	Page<Pessoa> paginarPessoasPorFiltroPesquisaSimples(String search, Pageable pageable);

	Optional<Pessoa> findBySub(String sub);

	boolean existsByEmail(String email);

	boolean existsByCpf(String cpf);
}
