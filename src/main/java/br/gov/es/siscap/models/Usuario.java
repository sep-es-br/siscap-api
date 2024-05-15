package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Table(name = "usuario")
@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String senha;
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> papeis;
    @OneToOne
    @SQLJoinTableRestriction("apagado = FALSE")
    @JoinColumn(name = "id_pessoa")
    private Pessoa pessoa;
    private String subNovo;
    @Setter
    private String accessToken;

    public Usuario(String senha, Set<String> papeis, Pessoa pessoa, String subNovo, String accessToken) {
        this.senha = senha;
        this.papeis = papeis;
        this.pessoa = pessoa;
        this.subNovo = subNovo;
        this.accessToken = accessToken;
    }

    /**
     *
     * @return vazio, porque as authorities são controladas na classe {@link br.gov.es.siscap.config.security.SecurityFilter}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return subNovo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
