package br.gov.es.siscap.dto.opcoes;

import br.gov.es.siscap.models.TipoMotivoArquivamento;

public record MotivosArquivamentoOpcoesDto( Long id, String tipo, String codigo
) {
	
	public MotivosArquivamentoOpcoesDto( TipoMotivoArquivamento tipoMotivoArquivamento ) {
		this( tipoMotivoArquivamento.getId(), tipoMotivoArquivamento.getTipo(), tipoMotivoArquivamento.getCodigo() );
	}

}