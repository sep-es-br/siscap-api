package br.gov.es.siscap.service;

import org.springframework.stereotype.Service;

import br.gov.es.siscap.enums.StatusProjetoEnum;
import br.gov.es.siscap.models.Projeto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegrasDePermissaoService {

    private final UsuarioService usuarioService;
    
    public boolean podeEditar(String subUsuario, Projeto projeto) {
        // regra: só SUBCAP pode editar se projeto está "Em Análise"
        boolean ehDaSubcap = usuarioService.ehDaSubcap(subUsuario);
        return ehDaSubcap && StatusProjetoEnum.COMPLEMETACAO.getValue().equals(projeto.getStatus());
    }

    public boolean podeSolicitarComplementacao(String subUsuario, Projeto projeto) {
        boolean ehDaSubcap = usuarioService.ehDaSubcap(subUsuario);
        return ehDaSubcap && StatusProjetoEnum.EM_ANALISE.getValue().equals(projeto.getStatus());
    }

    public boolean podeReenviarDICEmComplementacao(Boolean ehResponsavelProponente, Projeto projeto) {
        return ehResponsavelProponente && StatusProjetoEnum.COMPLEMETACAO.getValue().equals(projeto.getStatus());
    }

}
