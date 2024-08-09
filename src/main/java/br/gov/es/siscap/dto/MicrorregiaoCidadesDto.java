package br.gov.es.siscap.dto;

import java.util.List;

public class MicrorregiaoCidadesDto {

	private Long id;
	private String nome;
	private List<SelectDto> cidades;

	public MicrorregiaoCidadesDto() {

	}

//	public MicrorregiaoCidadesDto(Long id, String nome, List<SelectDto> cidades) {
//		this.id = id;
//		this.nome = nome;
//		this.cidades = cidades;
//	}

	public MicrorregiaoCidadesDto(SelectDto microrregiaoDto) {
		this.id = microrregiaoDto.id();
		this.nome = microrregiaoDto.nome();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<SelectDto> getCidades() {
		return cidades;
	}

	public void setCidades(List<SelectDto> cidades) {
		this.cidades = cidades;
	}
}
