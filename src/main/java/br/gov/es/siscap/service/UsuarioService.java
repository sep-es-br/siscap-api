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

    @Value("${api.parecer.guidSUBEPP}")
    private String guidSUBEPP;

    @Value("${api.parecer.guidSUBEO}")
    private String guidSUBEO;

    @Value("${api.parecer.lotacao.simulada}")
    private String lotacaoSimulada;

    private final UsuarioRepository repository;
    private final AcessoCidadaoService acessoCidadaoService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findBySub(username);
    }

    public void excluirPorPessoa(Long id) {
        repository.deleteByPessoa(new Pessoa(id));
    }

    public Boolean ehDaSubcap(String subUsuario) {
        String lotacaoGuidUsuario = this.lotacaoGuidUsuario(subUsuario);
        return lotacaoGuidUsuario.equalsIgnoreCase(guidSUBCAP);
    }

    public String lotacaoGuidUsuario(String subUsuario) {

        // ⚙️ Simulação de ambiente de teste
        if (lotacaoSimulada != null && !lotacaoSimulada.isEmpty()) {
            return switch (lotacaoSimulada.toUpperCase()) {
                case "SUBEPP" -> guidSUBEPP;
                case "SUBEO" -> guidSUBEO;
                default -> lotacaoSimulada;
            };
        }
        
        List<ACAgentePublicoPapelDto> listaPapeisUsuario = acessoCidadaoService
                .listarPapeisAgentePublicoPorSub(subUsuario);

        return listaPapeisUsuario.stream()
                .filter(papel -> Boolean.TRUE.equals(papel.Prioritario()))
                .findFirst()
                .map(ACAgentePublicoPapelDto::LotacaoGuid)
                .orElseGet(() -> listaPapeisUsuario.stream()
                        .findFirst()
                        .map( ACAgentePublicoPapelDto::LotacaoGuid )
                        .orElse(""));

    }

}
