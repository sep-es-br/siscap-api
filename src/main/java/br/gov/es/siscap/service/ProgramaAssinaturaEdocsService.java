package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProgramaAssinaturaEdocsDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaAssinaturaEdocs;
import br.gov.es.siscap.repository.ProgramaAssinaturaEdocsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramaAssinaturaEdocsService {

	@Value("${api.programa.assinantes.gestorSUBCAP}")
	private String assinanteEdocsProgramaGestorSUBCAP;

	@Value("${api.programa.assinantes.gestorSEP}")
	private String assinanteEdocsProgramaGestorSEP;

	@Value("${api.programa.assinantes.gestorGOVES}")
	private String assinanteEdocsProgramaGestorGOVES;

	private final ProgramaAssinaturaEdocsRepository repository;
	private final PessoaService pessoaService;
	private final AcessoCidadaoService acessoCidadaoService;

	private final Logger logger = LogManager.getLogger(ProgramaPessoaService.class);

	public List<ProgramaAssinaturaEdocsDto> buscarPorPrograma(Programa programa) {

		List<ProgramaAssinaturaEdocs> programaAssinaturaEdocsList = this
				.buscarProgramaAssinaturasSetPorPrograma(programa).stream().toList();

		Map<String, String> papelPorSub = new HashMap<>();
		Map<String, String> mensagemRecusaAssinaturaPorSub = new HashMap<>();

		programaAssinaturaEdocsList.stream()
				.forEach(assinante -> {

					List<ACAgentePublicoPapelDto> papeisAgentePublico = acessoCidadaoService
							.listarPapeisAgentePublicoPorSub(assinante.getPessoa().getSub());

					String nomePapelAssinante = papeisAgentePublico.stream()
							.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
							.findFirst()
							.map(ACAgentePublicoPapelDto::Nome)
							.orElseGet(() -> papeisAgentePublico.stream()
									.findFirst()
									.map(ACAgentePublicoPapelDto::Nome)
									.orElse(""));

					papelPorSub.put(assinante.getPessoa().getSub(), nomePapelAssinante);

					String mensagemRecusaAssinaturaAssinante = resolverMensagemRecusaAssinanteGestor(
							assinante.getPessoa().getSub());

					mensagemRecusaAssinaturaPorSub.put(assinante.getPessoa().getSub(),
							mensagemRecusaAssinaturaAssinante);

				});

		return this.montarListaDto(programaAssinaturaEdocsList, papelPorSub, mensagemRecusaAssinaturaPorSub);

	}

	public String resolverMensagemRecusaAssinanteGestor(String subAssinante) {

		if (subAssinante.equals(assinanteEdocsProgramaGestorGOVES))
			return "Após análise da proposta apresentada, não foi autorizada, nesta etapa, a continuidade dos procedimentos para prospecção de financiamento do programa, por decisão da autoridade competente.";
		else if (subAssinante.equals(assinanteEdocsProgramaGestorSEP))
			return "Após análise da proposta apresentada, não foi autorizada, nesta etapa, a continuidade dos procedimentos para prospecção de financiamento do programa, por decisão da Secretaria de Economia e Planejamento.";
		else if (subAssinante.equals(assinanteEdocsProgramaGestorSUBCAP))
			return "Após análise da proposta apresentada, não foi autorizada, nesta etapa, a continuidade dos procedimentos para prospecção de financiamento do programa, no âmbito da Subsecretaria de Captação de Recursos.";
		else
			return "Após análise da proposta apresentada, não foi autorizada, nesta etapa, a continuidade dos procedimentos para prospecção de financiamento do programa.";

	}

	@Transactional
	public List<ProgramaAssinaturaEdocsDto> cadastrar(Programa programa, List<String> assinantes) {

		logger.info("Cadastrando assinaturas do Programa com id: {}", programa.getId());

		Set<ProgramaAssinaturaEdocs> programaPessoasAssinantesSet = assinantes.stream()
				.map(assinante -> {
					Pessoa pessoa = pessoaService.buscarPorSub(assinante);
					return new ProgramaAssinaturaEdocs(programa, pessoa);
				})
				.collect(Collectors.toSet());

		List<ProgramaAssinaturaEdocs> programaPessoaAssinanteEdocsList = repository
				.saveAll(programaPessoasAssinantesSet);

		logger.info("Assinantes do programa cadastrados com sucesso");

		return this.montarListaDto(programaPessoaAssinanteEdocsList);

	}

	private List<ProgramaAssinaturaEdocsDto> montarListaDto(
			List<ProgramaAssinaturaEdocs> programaPessoaAssinanteEdocsList) {
		return programaPessoaAssinanteEdocsList.stream()
				.map(ProgramaAssinaturaEdocsDto::new)
				.toList();
	}

	private List<ProgramaAssinaturaEdocsDto> montarListaDto(
			List<ProgramaAssinaturaEdocs> programaPessoaAssinanteEdocsList,
			Map<String, String> papelPorSub, Map<String, String> textoRecusaPorSub) {

		return programaPessoaAssinanteEdocsList.stream()
				.map(assinante -> new ProgramaAssinaturaEdocsDto(
						assinante.getId(),
						assinante.getPrograma().getId(),
						assinante.getPessoa().getId(),
						assinante.getStatusAssinatura(),
						assinante.getDataAssinatura(),
						assinante.getPessoa().getNome(),
						papelPorSub.getOrDefault(assinante.getPessoa().getSub(), ""),
						textoRecusaPorSub.getOrDefault(assinante.getPessoa().getSub(), "")))
				.toList();

	}

	private Set<ProgramaAssinaturaEdocs> buscarProgramaAssinaturasSetPorPrograma(Programa programa) {
		return repository.findAllByPrograma(programa);
	}

}
