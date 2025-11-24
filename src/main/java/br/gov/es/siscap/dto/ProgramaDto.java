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
			BigDecimal valorCalculadoTotal

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
					programa.getValorCalculadoTotal()
		);
	}
}