package br.gov.es.siscap.infra;

import br.gov.es.siscap.enums.Permissoes;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@ToString
@Component
@Getter
public class Roles {

    private Map<String, List<Permissoes>> roles;

    public List<SimpleGrantedAuthority> getAuthorities(String role) {
        return roles.get(role).stream().map(r -> new SimpleGrantedAuthority(r.name())).toList();
    }

}

