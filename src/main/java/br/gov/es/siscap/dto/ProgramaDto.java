package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Programa;

import java.math.BigDecimal;
import java.util.List;

public record ProgramaDto(
		Long id,
		String sigla,
		String titulo,
		List<Long> idOrgaoExecutorList,
		List<EquipeDto> equipeCaptacao,
		List<Long> idProjetoPropostoList,
		ValorDto valor,
		BigDecimal percentualCustoAdministrativo,
		BigDecimal valorCalculadoTotal,
		List<ProgramaAssinaturaEdocsDto> programaAssinantesEdocsDto,
		String protocoloEdocs,
		String idDocumentoCapturadoEdocs
) {

	public ProgramaDto(Programa programa, List<EquipeDto> equipeCaptacao, List<Long> idProjetoPropostoList) {
		this(
				programa.getId(),
				programa.getSigla(),
				programa.getTitulo(),
				programa.getOrgaoExecutorSet().stream().map(Organizacao::getId).toList(),
				equipeCaptacao,
				idProjetoPropostoList,
				new ValorDto(programa.getTetoQuantia(), programa.getTipoValor().getId(), programa.getMoeda()),
				programa.getPercentualCustoAdministrativo(),
				programa.getValorCalculadoTotal(),
				programa.getProgramaAssinantesEdocsSet() == null
						? null
						: programa.getProgramaAssinantesEdocsSet().stream()
								.map(assinante -> new ProgramaAssinaturaEdocsDto(
										assinante.getId(),
										assinante.getPrograma().getId(),
										assinante.getPessoa().getId(),
										assinante.getStatusAssinatura(),
										assinante.getDataAssinatura(),
										assinante.getPessoa().getNome(),
										""))
								.toList(),
				programa.getProtocoloEdocs(),
				programa.getIdDocumentoCapturadoEdocs());
	}


	public ProgramaDto(Programa programa, List<EquipeDto> equipeCaptacao, List<Long> idProjetoPropostoList, List<ProgramaAssinaturaEdocsDto> assinantesProgramaListDto) {
		this(
				programa.getId(),
				programa.getSigla(),
				programa.getTitulo(),
				programa.getOrgaoExecutorSet().stream().map(Organizacao::getId).toList(),
				equipeCaptacao,
				idProjetoPropostoList,
				new ValorDto(programa.getTetoQuantia(), programa.getTipoValor().getId(), programa.getMoeda()),
				programa.getPercentualCustoAdministrativo(),
				programa.getValorCalculadoTotal(),
				assinantesProgramaListDto,
				programa.getProtocoloEdocs(),
				programa.getIdDocumentoCapturadoEdocs());
	}

}