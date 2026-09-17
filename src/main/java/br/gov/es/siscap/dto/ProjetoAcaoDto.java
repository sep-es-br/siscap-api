package br.gov.es.siscap.dto;

import java.math.BigDecimal;
import java.util.List;

import br.gov.es.siscap.models.ProjetoAcao;
import jakarta.validation.Valid;

public record ProjetoAcaoDto(

		Integer idAcao,
		String descricaoAcaoPrincipal,
		BigDecimal valorEstimadoAcaoPrincipal,
		String descricaoAcaoSecundaria,
		Long idStatus,

		@Valid List<RateioDto> rateio

) {

	public ProjetoAcaoDto(ProjetoAcao projetoAcao) {
		this(
				projetoAcao.getId(),
				projetoAcao.getDescricaoAcaoPrincipal(),
				projetoAcao.getValorEstimado(),
				projetoAcao.getDescricaoAcaoSecundaria(),
				projetoAcao.getProjeto().getTipoStatus().getId(),
				projetoAcao.getRateios()
						.stream()
						.map(rateio -> new RateioDto(
								rateio.getLocalidade().getId(),
								rateio.getPercentual(),
								rateio.getQuantia()))
						.toList());
	}

}
