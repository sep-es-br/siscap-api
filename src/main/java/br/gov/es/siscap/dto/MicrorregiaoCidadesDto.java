package br.gov.es.siscap.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MicrorregiaoCidadesDto {

	private Long id;
	private String nome;
	private List<SelectDto> cidades;

	public MicrorregiaoCidadesDto(SelectDto microrregiaoDto) {
		this.id = microrregiaoDto.id();
		this.nome = microrregiaoDto.nome();
	}
}
