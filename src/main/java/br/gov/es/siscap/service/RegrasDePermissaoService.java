package br.gov.es.siscap.service;

import org.springframework.stereotype.Service;

import br.gov.es.siscap.enums.StatusProjetoEnum;
import br.gov.es.siscap.models.Projeto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegrasDePermissaoService {

    private final UsuarioService usuarioService;

    public boolean podeEditar(String suUsuario, Projeto projeto) {
        // regra: só SUBCAP pode editar se projeto está "Em Análise"
        boolean ehDaSubcap = usuarioService.ehDaSubcap(suUsuario);
        return ehDaSubcap && StatusProjetoEnum.EM_ANALISE.getValue().equals(projeto.getStatus());
    }

    public boolean podeSolicitarComplementacao(String suUsuario, Projeto projeto) {
        boolean ehDaSubcap = usuarioService.ehDaSubcap(suUsuario);
        return ehDaSubcap && StatusProjetoEnum.EM_ANALISE.getValue().equals(projeto.getStatus());
    }

}
