package br.gov.es.siscap.dto.acessocidadaoapi;

public record ACAgentePublicoPapelDto(

			String Guid,
			String Nome,
			String Tipo,
			String LotacaoGuid,
			String AgentePublicoSub,
			String AgentePublicoNome,
			boolean Prioritario
) {
}