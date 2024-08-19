package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record ProjetoDto(
			Long id,
			String sigla,
			String titulo,
			BigDecimal valorEstimado,
			String objetivo,
			String objetivoEspecifico,
			Long idStatus,
			Long idOrganizacao,
			String situacaoProblema,
			String solucoesPropostas,
			String impactos,
			String arranjosInstitucionais,
//			List<Long> idMicrorregioes,
			List<RateioDto> rateio,
			Long idResponsavelProponente,
			List<EquipeDto> equipeElaboracao) {

//	public ProjetoDto(Projeto projeto) {
//		this(projeto.getId(), projeto.getSigla(), projeto.getTitulo(), projeto.getValorEstimado(),
//					projeto.getObjetivo(), projeto.getObjetivoEspecifico(), projeto.getStatus().getId(),
//					projeto.getOrganizacao().getId(), projeto.getSituacaoProblema(), projeto.getSolucoesPropostas(),
//					projeto.getImpactos(), projeto.getArranjosInstitucionais(),
//					null, List.of());
//					projeto.getMicrorregioes().stream().map(Microrregiao::getId).toList(),
//					projeto.getResponsavelProponente(), projeto.getEquipeElaboracao());
//	}

//	public static ProjetoDto comProjetoPessoa(Projeto projeto, Set<ProjetoPessoa> projetoPessoaSet) {
//		Long idResponsavelProponente = projetoPessoaSet.stream()
//					.filter(ProjetoPessoa::isResponsavelProponente)
//					.findFirst()
//					.map(ProjetoPessoa::getPessoa)
//					.map(Pessoa::getId)
//					.orElse(null);
//
//		List<EquipeDto> equipeElaboracao = projetoPessoaSet.stream()
//					.filter(Predicate.not(ProjetoPessoa::isResponsavelProponente))
//					.map(EquipeDto::new)
//					.collect(Collectors.toList());
//
//		return new ProjetoDto(projeto.getId(), projeto.getSigla(), projeto.getTitulo(), projeto.getValorEstimado(),
//					projeto.getObjetivo(), projeto.getObjetivoEspecifico(), projeto.getStatus().getId(),
//					projeto.getOrganizacao().getId(), projeto.getSituacaoProblema(), projeto.getSolucoesPropostas(),
//					projeto.getImpactos(), projeto.getArranjosInstitucionais(),
//					idResponsavelProponente, equipeElaboracao);
//	}

	public static ProjetoDto montar(Projeto projeto, Set<ProjetoPessoa> projetoPessoaSet, Set<ProjetoCidade> projetoCidadeSet) {
		Long idResponsavelProponente = projetoPessoaSet.stream()
					.filter(ProjetoPessoa::isResponsavelProponente)
					.findFirst()
					.map(ProjetoPessoa::getPessoa)
					.map(Pessoa::getId)
					.orElse(null);

		List<EquipeDto> equipeElaboracao = projetoPessoaSet.stream()
					.filter(Predicate.not(ProjetoPessoa::isResponsavelProponente))
					.map(EquipeDto::new)
					.toList();

		List<RateioDto> rateio = projetoCidadeSet.stream()
					.map(RateioDto::new)
					.toList();

		return new ProjetoDto(
					projeto.getId(),
					projeto.getSigla(),
					projeto.getTitulo(),
					projeto.getValorEstimado(),
					projeto.getObjetivo(),
					projeto.getObjetivoEspecifico(),
					projeto.getStatus().getId(),
					projeto.getOrganizacao().getId(),
					projeto.getSituacaoProblema(),
					projeto.getSolucoesPropostas(),
					projeto.getImpactos(),
					projeto.getArranjosInstitucionais(),
					rateio,
					idResponsavelProponente,
					equipeElaboracao);
	}

//	public ProjetoDto(Projeto projeto, Set<ProjetoPessoa> projetoPessoaSet) {
//		this(projeto.getId(), projeto.getSigla(), projeto.getTitulo(), projeto.getValorEstimado(),
//					projeto.getObjetivo(), projeto.getObjetivoEspecifico(), projeto.getStatus().getId(),
//					projeto.getOrganizacao().getId(), projeto.getSituacaoProblema(), projeto.getSolucoesPropostas(),
//					projeto.getImpactos(), projeto.getArranjosInstitucionais(),
////					projeto.getMicrorregioes().stream().map(Microrregiao::getId).toList(),
//					projetoPessoaSet.stream().filter(projetoPessoa -> projetoPessoa.getPapel().getId() == 2L)
//								.findFirst().map(projetoPessoa -> projetoPessoa.getPessoa().getId()).orElse(null),
//					projetoPessoaSet.stream().filter(projetoPessoa -> projetoPessoa.getPapel().getId() != 2L).map(EquipeDto::new).toList());
//	}
}
