package br.gov.es.siscap.dto;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import java.util.List;

public record ProgramaInformacoesDicsDto(List<ProgramaDicDto> dics, List<OpcoesDto> regioesAtendidas) {
}
