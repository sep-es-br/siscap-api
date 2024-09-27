package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.*;

import java.math.BigDecimal;
import java.util.List;

public record ProjetoDto(

			Long id,
			String sigla,
			String titulo,
			BigDecimal valorEstimado,
			String objetivo,
			String objetivoEspecifico,
			Long idStatus,
			Long idOrganizacao,
			String situacaoProblema,
			String solucoesPropostas,
			String impactos,
			String arranjosInstitucionais,
			RateioDto rateio,
			Long idResponsavelProponente,
			List<EquipeDto> equipeElaboracao
) {

	public ProjetoDto(Projeto projeto, RateioDto rateio, Long idResponsavelProponente, List<EquipeDto> equipeElaboracao) {
		this(
					projeto.getId(),
					projeto.getSigla(),
					projeto.getTitulo(),
					projeto.getValorEstimado(),
					projeto.getObjetivo(),
					projeto.getObjetivoEspecifico(),
					projeto.getStatus().getId(),
					projeto.getOrganizacao().getId(),
					projeto.getSituacaoProblema(),
					projeto.getSolucoesPropostas(),
					projeto.getImpactos(),
					projeto.getArranjosInstitucionais(),
					rateio,
					idResponsavelProponente,
					equipeElaboracao
		);
	}
}
