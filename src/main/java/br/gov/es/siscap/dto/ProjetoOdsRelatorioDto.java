package br.gov.es.siscap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjetoOdsRelatorioDto {

    private Integer idOdsProjeto;
    private Integer odsId;
    private Integer odsOrdem;
    private String odsNome;
    private String odsDescricao;
    private String odsCor;
}
