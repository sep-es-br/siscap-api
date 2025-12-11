package br.gov.es.siscap.utils;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class EnvioPedidoParecerOrcamentarioEstrategicoEmailBuilder extends EmailBuilderBase {

	String siglaProjeto;

	@Override
	protected String montarCampoTratamento(EnvioEmailDicDetalhesDto dto) {
		return "Prezados(as)";
	}

	@Override
	public String montarAssuntoEmail() {
		return "Pedido de parecer do DIC %s".formatted(this.getSiglaProjeto());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) {
		return "Solicitamos que seja elaborado o parecer necessário conforme sua área para o DIC via link abaixo.";
	}

}
