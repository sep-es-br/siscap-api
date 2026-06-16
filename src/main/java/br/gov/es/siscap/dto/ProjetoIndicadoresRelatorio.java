package br.gov.es.siscap.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjetoIndicadoresRelatorio {

    String nomeIndicador;

    String unidadeMedida;

    String fonteIndicador;

    String medidoPor;

    String baseDeReferencia;

    String formulaCalculo;

    List<IndicadorMetaRelatorioDto> metasIndicador;

}


