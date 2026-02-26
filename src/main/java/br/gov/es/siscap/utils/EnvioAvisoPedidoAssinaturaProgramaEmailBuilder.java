package br.gov.es.siscap.utils;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
public class EnvioAvisoPedidoAssinaturaProgramaEmailBuilder extends EmailBuilderBase {

	@Value("${api.programa.assinantes.gestorSUBCAP}")
	private String assinanteEdocsProgramaGestorSUBCAP;

	@Value("${api.programa.assinantes.gestorSEP}")
	private String assinanteEdocsProgramaGestorSEP;

	@Value("${api.programa.assinantes.gestorGOVES}")
	private String assinanteEdocsProgramaGestorGOVES;

	private Map<String, String> subEmailDestinatarios;
	private String emailEmProcessamento;
	private String nomeDestinatario;

	@Override
	protected String montarCampoTratamento(EnvioEmailDetalhesDto dto) {

		return "Prezado(a) senhor(a)<br>" + this.getNomeDestinatario();
				
	}

	@Override
	public String montarAssuntoEmail() {
		return "Autorizar programa para captação.";
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDetalhesDto dto) {
		return ("A minuta do programa de captação de recursos <b>%s</b> - <b>%s</b> foi criada no Siscap - Sistema de Captação de Recursos do Estado do Espírito Santo - e aguarda a sua análise e autorização, se de acordo.")
				.formatted(Objects.toString(dto.siglaPrograma(), ""),
						Objects.toString(dto.tituloPrograma(), ""));
	}

	@Override
	protected String montarLinkAcesso(EnvioEmailDetalhesDto dto) {

		String frontEndHost = this.getEnv().getProperty("frontend.host");

		if (frontEndHost == null || frontEndHost.isBlank()) {
			return "";
		}

		String linkEdicao = frontEndHost.replaceAll("/$", "") + "/main/programas/" + dto.idPrograma() + "/assinaturas";

		if (StringUtils.isBlank(linkEdicao)) {
			return "";
		}

		return """
				    <p style="font-size: 12px;" >Acesse e autorize em <a style="font-size: 12px;" href="%s">%s</a> </p>
				"""
				.formatted(linkEdicao, linkEdicao);

	}

}
