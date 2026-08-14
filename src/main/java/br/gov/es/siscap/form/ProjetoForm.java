package br.gov.es.siscap.form;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProjetoAcaoDto;
import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoDto;
import br.gov.es.siscap.dto.ProjetoIndicadorDto;
import br.gov.es.siscap.dto.ProjetoParecerDto;
import br.gov.es.siscap.dto.ProjetoPlanejamentoPpaLoaDto;
import br.gov.es.siscap.dto.RateioDto;
import br.gov.es.siscap.dto.ValorDto;
import br.gov.es.siscap.dto.ProjetoOdsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record ProjetoForm(

		@NotBlank 
		@Size(max = 12) 
		String sigla,

		@NotBlank 
		@Size(max = 150) 
		String titulo,

		@NotNull 
		@Positive 
		Long idResponsavelProponente,

		@NotEmpty 
		@Valid 
		List<EquipeDto> equipeElaboracao,

		@Positive 
		Long idOrganizacao,

		@Valid 
		ValorDto valor,

		@Valid 
		List<RateioDto> rateio,

		@Size(max = 2000) 
		String objetivo,

		@Size(max = 2000) 
		String objetivoEspecifico,

		@Size(max = 2000) 
		String situacaoProblema,

		@Size(max = 2000) 
		String solucoesPropostas,

		@Size(max = 2000) 
		String impactos,

		@Size(max = 2000) 
		String arranjosInstitucionais,

		@Valid 
		List<ProjetoIndicadorDto> indicadoresProjeto,

		@Valid 
		List<ProjetoAcaoDto> acoesProjeto,

		@Size(max = 2000) 
		String pecasPlanejamento,

		boolean enviarProjetoGestor,

		@Size(max = 15) 
		String protocoloEdocs,

		boolean 
		enviarProjetoPedirParecer,

		@Valid 
		ProjetoParecerDto parecerProjetoUsuario,

		@Valid 
		List<ProjetoIndicadorAvulsoDto> indicadoresAvulsosProjeto,

		@Valid 
		List<ProjetoOdsDto> odsProjeto,

		@Valid 
		List<ProjetoPlanejamentoPpaLoaDto> acoesPlanejamentoProjeto,

		Boolean naoPrevistoNoPpa

) { }