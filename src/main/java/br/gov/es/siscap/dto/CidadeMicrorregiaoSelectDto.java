package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Cidade;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CidadeMicrorregiaoSelectDto {

	private Long id;
	private String nome;
	private Long idMicrorregiao;

	public CidadeMicrorregiaoSelectDto(Cidade cidade) {
		this.id = cidade.getId();
		this.nome = cidade.getNome();
		this.idMicrorregiao = cidade.getMicrorregiao().getId();
	}
}
