package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    @Value("${api.edocs.guiddestinoSUBCAP}")
	private String guidSUBCAP;

    private final UsuarioRepository repository;
    private final AcessoCidadaoService acessoCidadaoService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findBySub(username);
    }

    public void excluirPorPessoa(Long id) {
        repository.deleteByPessoa(new Pessoa(id));
    }

    public Boolean ehDaSubcap(String subUsuario ){
        List<ACAgentePublicoPapelDto> listaPapeisUsuario = acessoCidadaoService.listarPapeisAgentePublicoPorSub(subUsuario);
        String lotacaoGuidUsuario = listaPapeisUsuario.stream()
                        .filter( papel -> Boolean.TRUE.equals(papel.Prioritario()))
                        .findFirst()
                        .map(ACAgentePublicoPapelDto::LotacaoGuid)
                        .orElseGet( () -> listaPapeisUsuario.stream()
                            .findFirst()
                            .map(ACAgentePublicoPapelDto::LotacaoGuid)
                            .orElse(""));
       return lotacaoGuidUsuario.equalsIgnoreCase(guidSUBCAP);
    }
    

}
