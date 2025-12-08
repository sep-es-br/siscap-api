package br.gov.es.siscap.utils;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnvioArquivamentoDicEmailBuilder extends EmailBuilderBase {

	String siglaProjeto;

	public EnvioArquivamentoDicEmailBuilder(EnvioEmailDicDetalhesDto dto) {
		super(dto);
	}

	@Override
	protected String montarCampoTratamento(EnvioEmailDicDetalhesDto dto) {
		return "Prezado(a) %s".formatted(dto.nomeResponsavelEnvioEmail());
	}

	@Override
	protected String montarLinkAcesso(EnvioEmailDicDetalhesDto dto) {

		if (dto.linkAcessoProjeto() == null || dto.linkAcessoProjeto().isBlank()) {
			return "";
		}

		return """
				    <p>Acesse o sistema SISCAP em:</p>
				    <a href="%s">%s</a>
				""".formatted(dto.linkAcessoProjeto(), dto.tituloProjeto());

	}

	@Override
	public String montarAssuntoEmail() {
		return "Arquivamento do DIC %s".formatted(this.getSiglaProjeto());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) {

		String corpoEmail = "Informamos que o DIC [<strong>%S</strong>] será arquivado conforme avaliação técnica e gerencial realizada pelo gestor [<strong>%S</strong>]."
				.formatted(dto.tituloProjeto(), dto.nomeGestor());

		String pontosRevisar = "<p style=\"font-size: 14px; color: #000000; margin-bottom: 5px;\">"
				+ "<strong> Motivo: </strong>"
				+ " </p> <br> <p style=\"font-size: 14px; color: #000000; margin-bottom: 5px;\" > "
				+ dto.codigoMotivoArquivamento() + " - " + dto.descricaoTipoMotivoArquivamento() + " - "
				+ dto.justificaivaArquivamento()
				+ " </p> ";

		return corpoEmail + pontosRevisar;

	}

}
