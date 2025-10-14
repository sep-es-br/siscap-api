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

        boolean podeEditar = false;
        boolean ehDaSubcap = usuarioService.ehDaSubcap(subUsuario);

        if (ehDaSubcap) {
            if (StatusProjetoEnum.EM_ANALISE.getValue().equals(projeto.getStatus())) {
                podeEditar = true;
            } else {
                podeEditar = false;
            }
        } else if (!ehDaSubcap) {
            if (StatusProjetoEnum.COMPLEMETACAO.getValue().equals(projeto.getStatus())) {
                podeEditar = true;
            } else {
                podeEditar = false;
            }
        }

        if (StatusProjetoEnum.EM_ELABORACAO.getValue().equals(projeto.getStatus()))
            podeEditar = true;

        if (StatusProjetoEnum.ARQUIVADO.getValue().equals(projeto.getStatus()))
            podeEditar = false;

        return podeEditar;

    }

    public boolean podeSolicitarComplementacao(String subUsuario, Projeto projeto) {
        boolean ehDaSubcap = usuarioService.ehDaSubcap(subUsuario);
        return ehDaSubcap && StatusProjetoEnum.EM_ANALISE.getValue().equals(projeto.getStatus());
    }

    public boolean podeReenviarDICEmComplementacao(Boolean ehResponsavelProponente, Projeto projeto) {
        return ehResponsavelProponente && StatusProjetoEnum.COMPLEMETACAO.getValue().equals(projeto.getStatus());
    }

}
