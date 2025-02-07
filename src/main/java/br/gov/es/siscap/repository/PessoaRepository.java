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
				"where " +
				" p.apagado = false and " +
				" (lower(p.nome) like lower(concat('%', :search, '%')) or " +
				" lower(p.email) like lower(concat('%', :search, '%')))"
	)
	Page<Pessoa> paginarPessoasPorFiltroPesquisaSimples(String search, Pageable pageable);

	@Query(value = "select p.* from Pessoa p " +
				"where " +
				" p.apagado = false and " +
				" (public.comparar_nome_pessoa(p.nome, :nome) or " +
				" p.sub = :sub)",
				nativeQuery = true
	)
	Optional<Pessoa> buscarPorSubOuNomeTratado(String sub, String nome);

	Optional<Pessoa> findBySub(String sub);

	boolean existsByEmail(String email);

	boolean existsByCpf(String cpf);
}
