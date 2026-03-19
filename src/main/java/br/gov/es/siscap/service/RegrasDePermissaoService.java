package br.gov.es.siscap.service;

import br.gov.es.siscap.enums.StatusProjetoEnum;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Projeto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegrasDePermissaoService {

    private final UsuarioService usuarioService;

    public boolean podeEditar(String subUsuario, Projeto projeto ) {

        boolean podeEditar = false;
        boolean ehDaSubcap = usuarioService.ehDaSubcap(subUsuario);
        boolean ehResponsavelProponente = this.usuarioEhResponsavelProponente(subUsuario, projeto);

        if (ehDaSubcap) {
            if (StatusProjetoEnum.EM_ANALISE.getValue().equals(projeto.getStatusAtual().getStatus())) {
                podeEditar = true;
            } else {
                podeEditar = false;
            }
        } else if (!ehDaSubcap) {
            if (StatusProjetoEnum.COMPLEMETACAO.getValue().equals(projeto.getStatusAtual().getStatus())) {
                podeEditar = true;
            } else {
                podeEditar = false;
            }
        }

        if (StatusProjetoEnum.EM_ELABORACAO.getValue().equals(projeto.getStatusAtual().getStatus()))
            podeEditar = true;

        if (StatusProjetoEnum.ARQUIVADO.getValue().equals(projeto.getStatusAtual().getStatus()))
            podeEditar = false;

        if (StatusProjetoEnum.COMPLEMETACAO.getValue().equals(projeto.getStatusAtual().getStatus()) && ehResponsavelProponente )
            podeEditar = true;

        return podeEditar;

    }

    public boolean podeSolicitarComplementacao(String subUsuario, Projeto projeto) {
        boolean ehDaSubcap = usuarioService.ehDaSubcap(subUsuario);
        return ehDaSubcap && StatusProjetoEnum.EM_ANALISE.getValue().equals(projeto.getStatusAtual().getStatus());
    }

    public boolean podeReenviarDICEmComplementacao(Boolean ehResponsavelProponente, Projeto projeto) {
        return ehResponsavelProponente && StatusProjetoEnum.COMPLEMETACAO.getValue().equals(projeto.getStatusAtual().getStatus());
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
