package br.gov.es.siscap.utils;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;

public class EnvioAnaliseGestorDicEmailBuilder extends EmailBuilderBase {
	
	public EnvioAnaliseGestorDicEmailBuilder(EnvioEmailDicDetalhesDto dto) {
		super(dto);
	}

	@Override
    protected String montarCampoTratamento(EnvioEmailDicDetalhesDto dto) {
        return "Prezada(o) Gestora(or) do(a) <strong>" + dto.descricaoOrganizacaoGestor() + "</strong>";
    }

    @Override
    protected String montarLinkAcesso(EnvioEmailDicDetalhesDto dto) {
        return """
            <p>Acesse o sistema SISCAP em:</p>
            <a href="%s">%s</a>
        """.formatted(dto.linkAcessoProjeto(), dto.linkAcessoProjeto());
    }

	@Override
	public String montarAssuntoEmail() {
		return "DIC disponível para análise e autuação via E-Docs.";
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) {
		return "Informamos que há um DIC (Documento Inicial para Captação) disponível para análise e tramitação diretamente no SISCAP.<br><br>" +
		"Todo o acompanhamento, assinatura e andamento do processo devem ser realizados exclusivamente pelo sistema SISCAP, não sendo necessário acessar o E-Docs manualmente.";
	}


}
