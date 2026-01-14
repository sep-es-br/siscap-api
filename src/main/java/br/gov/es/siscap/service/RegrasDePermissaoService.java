package br.gov.es.siscap.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.gov.es.siscap.enums.StatusProjetoEnum;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Projeto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegrasDePermissaoService {

    private final UsuarioService usuarioService;

    public boolean podeEditar(String subUsuario, Projeto projeto ) {

        boolean podeEditar = false;
        boolean ehDaSubcap = usuarioService.ehDaSubcap(subUsuario);
        boolean ehResponsavelProponente = this.usuarioEhResponsavelProponente(subUsuario, projeto);

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

        if (StatusProjetoEnum.COMPLEMETACAO.getValue().equals(projeto.getStatus()) && ehResponsavelProponente )
            podeEditar = true;

        return podeEditar;

    }

    public boolean podeSolicitarComplementacao(String subUsuario, Projeto projeto) {
        boolean ehDaSubcap = usuarioService.ehDaSubcap(subUsuario);
        return ehDaSubcap && StatusProjetoEnum.EM_ANALISE.getValue().equals(projeto.getStatus());
    }

    public boolean podeReenviarDICEmComplementacao(Boolean ehResponsavelProponente, Projeto projeto) {
        return ehResponsavelProponente && StatusProjetoEnum.COMPLEMETACAO.getValue().equals(projeto.getStatus());
    }

    private boolean usuarioEhResponsavelProponente(String subUsuario, Projeto projeto){

        Optional<Pessoa> responsavelProponenteProjeto = projeto.getProjetoPessoaSet()
			.stream()
			.filter( membro -> membro.isResponsavelProponente() )
			.findFirst()
			.map( proponente -> proponente.getPessoa() );

		return responsavelProponenteProjeto
			.map(pessoa -> pessoa.getSub().equalsIgnoreCase(subUsuario))
        	.orElse(false);

    }

}
