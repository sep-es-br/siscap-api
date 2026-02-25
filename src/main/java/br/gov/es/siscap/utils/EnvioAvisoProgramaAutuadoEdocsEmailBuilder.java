package br.gov.es.siscap.utils;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class EnvioAvisoProgramaAutuadoEdocsEmailBuilder extends EmailBuilderBase {

	@Value("${api.programa.assinantes.gestorSUBCAP}")
	private String assinanteEdocsProgramaGestorSUBCAP;

	@Value("${api.programa.assinantes.gestorSEP}")
	private String assinanteEdocsProgramaGestorSEP;

	@Value("${api.programa.assinantes.gestorGOVES}")
	private String assinanteEdocsProgramaGestorGOVES;

	private Map<String, String> subEmailDestinatarios;
	private String emailEmProcessamento;

	@Override
	protected String montarCampoTratamento(EnvioEmailDetalhesDto dto) {
		
		// if( this.getSubEmailDestinatarios().get(emailEmProcessamento) == assinanteEdocsProgramaGestorSUBCAP )
		// 	return "Senhor(a) Subsecretário(a)<br>\r\n" + //
		// 		"ANDRESSA RODRIGUES PAVÃO\r\n";
		// else if( this.getSubEmailDestinatarios().get(emailEmProcessamento) == assinanteEdocsProgramaGestorSEP )
		// 	return  "Excelentíssimo(a) Senhor(a) Secretário(a)<br>\r\n" + //
		// 			"ÁLVARO ROGÉRIO DUBOC FAJARDO";
		// else if( this.getSubEmailDestinatarios().get(emailEmProcessamento) ==  assinanteEdocsProgramaGestorGOVES )
		// 	return "Excelentíssimo(a) Senhor(a) Governador(a)<br>\r\n" + //
		// 			"JOSÉ RENATO CASAGRANDE";
		// else 
			return "Prezado(a) senhor(a),";

	}

	@Override
	public String montarAssuntoEmail() {
		return "Programa autuado.";
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDetalhesDto dto) {
		return ("Informamos que a minuta do programa de captação de recursos <b>%s</b> - <b>%s</b> criada no Siscap - Sistema de Captação de Recursos do Estado do Espírito Santo - foi autuado com sucesso no E-Docs.")
				.formatted(Objects.toString(dto.siglaPrograma(), ""),
						Objects.toString(dto.tituloPrograma(), ""));
	}

	@Override
	protected String montarLinkAcesso(EnvioEmailDetalhesDto dto) {
		return "";
	}

}
