package br.gov.es.siscap.service;

import br.gov.es.siscap.config.security.AuthorizationRequestResolver;
import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Usuario;
import br.gov.es.siscap.repository.UsuarioRepository;
import br.gov.es.siscap.utils.OverrideProperties;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private final OverrideProperties overrideProperties;

    private final Logger logger = LogManager.getLogger(AuthorizationRequestResolver.class);

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

    public Boolean ehDaSubepp(String subUsuario) {
        String lotacaoGuidUsuario = this.lotacaoGuidUsuario(subUsuario);
        return lotacaoGuidUsuario.equalsIgnoreCase(guidSUBEPP);
    }

    public Boolean ehDaSubeo(String subUsuario) {
        String lotacaoGuidUsuario = this.lotacaoGuidUsuario(subUsuario);
        return lotacaoGuidUsuario.equalsIgnoreCase(guidSUBEO);
    }

    public String lotacaoGuidUsuario(String subUsuario) {

        String overrideLotacao = overrideProperties.getLotacaoUsuario().get(subUsuario);
        if (overrideLotacao != null) {
            logger.info("LOTACAO_RESOLVED source=userOverride guid={}", overrideLotacao);
            return overrideLotacao;
        }

        // ⚙️ Simulação de ambiente de teste
        if (lotacaoSimulada != null && !lotacaoSimulada.isEmpty()) {
            String guidResolvido = switch (lotacaoSimulada.toUpperCase()) {
                case "SUBEPP" -> guidSUBEPP;
                case "SUBEO" -> guidSUBEO;
                default -> lotacaoSimulada;
            };
            logger.info("LOTACAO_RESOLVED source=globalSimulation guid={}", guidResolvido);
            return guidResolvido;
        }

        Usuario usuarioBanco = (Usuario) this.repository.findBySub(subUsuario);

        if (usuarioBanco.getPapeis() != null && usuarioBanco.getPapeis().contains("SUBCAP")) {
            logger.info("LOTACAO_RESOLVED source=userRole_SUBCAP guid={}", guidSUBCAP);
            return guidSUBCAP;
        }

        List<ACAgentePublicoPapelDto> listaPapeisUsuario = acessoCidadaoService
                .listarPapeisAgentePublicoPorSub(subUsuario);

        String guidResolvido = listaPapeisUsuario.stream()
                .filter(papel -> Boolean.TRUE.equals(papel.Prioritario()))
                .findFirst()
                .map(ACAgentePublicoPapelDto::LotacaoGuid)
                .orElseGet(() -> listaPapeisUsuario.stream()
                        .findFirst()
                        .map(ACAgentePublicoPapelDto::LotacaoGuid)
                        .orElse(""));

        logger.info("LOTACAO_RESOLVED source=acessoCidadao guid={}", guidResolvido);
        return guidResolvido;

    }

    public Optional<ACAgentePublicoPapelDto> lotacaoUsuario(String subUsuario) {

        List<ACAgentePublicoPapelDto> listaPapeisUsuario = acessoCidadaoService
                .listarPapeisAgentePublicoPorSub(subUsuario);

        return Optional.ofNullable(listaPapeisUsuario.stream()
                .filter(papel -> Boolean.TRUE.equals(papel.Prioritario()))
                .findFirst()
                .orElseGet(() -> listaPapeisUsuario.stream()
                        .findFirst()
                        .orElse(null)));

    }

}
