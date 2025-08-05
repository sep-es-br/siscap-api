package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.opcoes.MotivosArquivamentoOpcoesDto;
import br.gov.es.siscap.models.TipoMotivoArquivamento;
import br.gov.es.siscap.repository.TipoMotivoArquivamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoMotivoArquivamentoService {

	private final TipoMotivoArquivamentoRepository repository;

	public List<MotivosArquivamentoOpcoesDto> listarOpcoesDropdown() {
		return repository.findAll().stream().map(MotivosArquivamentoOpcoesDto::new).toList();
	}

	public TipoMotivoArquivamento buscarTipoMotivoCodigo(String codigoTipoMotivo ){
		return repository.findByCodigo(codigoTipoMotivo).orElse(null);
	}

}