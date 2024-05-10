package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    UserDetails findByEmail(String email);
    void deleteByPessoa(Pessoa pessoa);
}
