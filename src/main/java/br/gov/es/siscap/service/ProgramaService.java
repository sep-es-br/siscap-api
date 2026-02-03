package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.acessocidadaoapi.EmailSubResponseDto;
import br.gov.es.siscap.dto.listagem.ProgramaListaDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.enums.TipoStatusAssinaturaEnum;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.form.ProgramaForm;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaAssinaturaEdocs;
import br.gov.es.siscap.repository.ProgramaAssinaturaEdocsRepository;
import br.gov.es.siscap.repository.ProgramaRepository;
import br.gov.es.siscap.utils.FormatadorCountAno;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramaService {

	private final ProgramaRepository repository;
	private final ProjetoService projetoService;
	private final ProgramaPessoaService programaPessoaService;
	private final PessoaService pessoaService;
	private final PessoaOrganizacaoService pessoaOrganizacaoService;
	private final AsyncExecutorService asyncExecutorService;
	private final ProgramaAssinaturaEdocsService programaAssinaturaEdocsService;
	private final EmailService emailService;
	private final AcessoCidadaoService acessoCidadaoService;
	private final IntegraccaoEdocsService integracaoEdocsService;
	private final ProgramaAssinaturaEdocsRepository programaAssinaturaEdocsRepository;

	private final Logger logger = LogManager.getLogger(ProgramaService.class);

	@Value("${api.programa.assinantes.gestorSUBCAP}")
	private String assinanteEdocsProgramaGestorSUBCAP;

	@Value("${api.programa.assinantes.gestorSEP}")
	private String assinanteEdocsProgramaGestorSEP;

	@Value("${api.programa.assinantes.gestorGOVES}")
	private String assinanteEdocsProgramaGestorGOVES;

	public Page<ProgramaListaDto> listarTodos(Pageable pageable, String search) {
		logger.info("Buscando todos os programas");

		return repository.paginarProgramasPorFiltroPesquisaSimples(search, pageable)
				.map(ProgramaListaDto::new);
	}

	public List<OpcoesDto> listarOpcoesDropdown() {
		return repository.findAll().stream().map(OpcoesDto::new).toList();
	}

	public ProgramaDto buscarPorId(Long id) {
		logger.info("Buscando programa com id: {}", id);

		Programa programa = this.buscar(id);

		List<EquipeDto> equipeCaptacao = programaPessoaService.buscarPorPrograma(programa);

		List<Long> idProjetoPropostoList = projetoService.buscarIdProjetoPropostoList(programa);

		return new ProgramaDto(programa, equipeCaptacao, idProjetoPropostoList);
	}

	@Transactional
	public ProgramaDto cadastrar(ProgramaForm form) {
		logger.info("Cadastrando novo programa");
		logger.info("Dados: {}", form);

		Programa tempPrograma = new Programa(form);

		tempPrograma.setCountAno(buscarCountAnoFormatado());

		Programa programa = repository.save(tempPrograma);

		List<EquipeDto> equipeParaGravar = form.equipeCaptacao();

		List<EquipeDto> equipeCapacitacaoValidada = this.validarEquipeCapacitacao(form);
		if (!new HashSet<>(form.equipeCaptacao()).equals(new HashSet<>(equipeCapacitacaoValidada))) {
			equipeParaGravar = equipeCapacitacaoValidada;
		}

		List<EquipeDto> equipeCaptacao = programaPessoaService.cadastrar(programa, equipeParaGravar);

		List<Long> idProjetoPropostoList = projetoService.vincularProjetosAoPrograma(programa,
				form.idProjetoPropostoList());

		logger.info("Programa cadastrado com sucesso");

		return new ProgramaDto(programa, equipeCaptacao, idProjetoPropostoList);
	}

	private List<EquipeDto> validarEquipeCapacitacao(ProgramaForm form) {

		List<EquipeDto> equipe = new ArrayList<>();

		for (EquipeDto membro : form.equipeCaptacao()) {

			String sub = membro.subPessoa();

			String id = pessoaService.buscarIdPorSub(sub);
			if (id.isBlank()) {
				logger.info("Pessoa com sub [{}] não encontrada na base do SISCAP, procedendo para criação.", sub);
				id = pessoaService.sincronizarAgenteCidadaoPessoaSiscap(sub);
			} else {
				logger.info("Verificar se dados da pessoa com sub [{}] estão batendo com dados da tabela pessoa.", sub);
				pessoaService.sincronizarDadosAgentePessoaSiscap(Long.valueOf(id), sub);
			}

			EquipeDto novoMembro = new EquipeDto(Long.valueOf(id), membro.idPapel(), membro.idStatus(),
					membro.justificativa(), membro.subPessoa(), membro.nome());

			equipe.add(novoMembro);

			logger.info("Verificar se pessoa com id [{}] possui organizacao associada na base do SISCAP.", id);

			List<PessoaOrganizacao> organizacoes = pessoaOrganizacaoService.buscarPorIds(List.of(Long.valueOf(id)));
			if (organizacoes.isEmpty()) {
				logger.info(
						"Pessoa com sub [{}] não possui organizacao associada na base do SISCAP - proceder com atualizacao do AC.",
						sub);
				Set<Organizacao> organizacoesAC = pessoaService.buscarOrganizacoesAssociadas(sub);
				pessoaService.associarOrganizacoesAPessoa(pessoaService.buscarPorSub(sub), organizacoesAC);
			}

		}

		return equipe;

	}

	@Transactional
	public ProgramaDto atualizar(Long id, ProgramaForm form) {

		logger.info("Atualizando programa com id: {}", id);
		logger.info("Dados: {}", form);

		Programa programa = this.buscar(id);

		programa.atualizar(form);

		Programa programaResult = repository.save(programa);

		List<EquipeDto> equipeParaGravar = form.equipeCaptacao();

		List<EquipeDto> equipeCapacitacaoValidada = this.validarEquipeCapacitacao(form);
		if (!new HashSet<>(form.equipeCaptacao()).equals(new HashSet<>(equipeCapacitacaoValidada))) {
			equipeParaGravar = equipeCapacitacaoValidada;
		}

		List<EquipeDto> equipeCaptacao = programaPessoaService.atualizar(programaResult, equipeParaGravar);

		List<Long> idProjetoPropostoList = projetoService.vincularProjetosAoPrograma(programaResult,
				form.idProjetoPropostoList());

		logger.info("Programa atualizado com sucesso");

		return new ProgramaDto(programaResult, equipeCaptacao, idProjetoPropostoList);

	}

	@Transactional
	public void excluir(Long id) {

		logger.info("Excluindo programa com id: {}", id);

		Programa programa = this.buscar(id);
		programa.apagar();
		repository.saveAndFlush(programa);

		programaPessoaService.excluirPorPrograma(programa);
		projetoService.desvincularProjetosDoPrograma(programa);

		logger.info("Programa excluído com sucesso");

	}

	public Integer buscarQuantidadeProgramas() {
		return Integer.parseInt(String.valueOf((repository.count())));
	}

	private Programa buscar(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Programa não encontrado"));
	}

	private String buscarCountAnoFormatado() {
		return FormatadorCountAno.formatar(repository.contagemAnoAtual());
	}

	public String gerarNomeArquivo(Long idPrograma) {

		Programa programa = this.buscar(idPrograma.longValue());

		return "PROGRAMA n. " +
				programa.getCountAno();
	}

	@Transactional
	public void criarArquivoProgramaEdocsAssinaturasPendentes(Long idPrograma) {
		String nomeArquivo = this.gerarNomeArquivo(idPrograma);
		List<String> assinantesEdocsPrograma = List.of(assinanteEdocsProgramaGestorSUBCAP,
				assinanteEdocsProgramaGestorSEP, assinanteEdocsProgramaGestorGOVES);
		this.marcarComoAguardandoAssinaturas(idPrograma, assinantesEdocsPrograma);
		asyncExecutorService.criarArquivoFaseAssinaturaEdocsServidor(idPrograma, assinantesEdocsPrograma, nomeArquivo);
		this.enviarAvisoSolicitarAssinaturaPrograma(idPrograma, assinantesEdocsPrograma);
	}

	private void marcarComoAguardandoAssinaturas(Long idPrograma, List<String> assinantesEdocsPrograma) {
		logger.info("Registra as pendencias de assinatura no programa;");
		Programa programa = this.buscar(idPrograma);
		if (programaAssinaturaEdocsService.buscarPorPrograma(programa).isEmpty()) {
			programaAssinaturaEdocsService.cadastrar(programa, assinantesEdocsPrograma);
		}
	}

	@Transactional
	public boolean enviarAvisoSolicitarAssinaturaPrograma(Long idPrograma, List<String> subAssinantes) {

		List<String> erros = new ArrayList<>();

		if (subAssinantes.isEmpty()) {
			erros.add("Erro ao enviar solicitação para assinatura do programa id " + idPrograma
					+ " assinaturas não informadas.");
			throw new ValidacaoSiscapException(erros);
		}

		List<String> emailsInteressadosList = new ArrayList<String>();

		// para cada sub vai buscar o email no acesso cidadao..
		subAssinantes.forEach(sub -> {

			EmailSubResponseDto emailsSub = acessoCidadaoService.buscarEmailsPorSub(sub);

			if (emailsSub.corporativo() != null && !emailsSub.corporativo().isBlank()) {
				emailsInteressadosList.add(emailsSub.corporativo());
			} else if (emailsSub.email() != null && !emailsSub.email().isBlank()) {
				emailsInteressadosList.add(emailsSub.email());
			}

		}

		);

		Programa programa = this.buscar(idPrograma);

		String tituloPrograma = programa.getTitulo();

		boolean confirmacaoEnvioEmail;

		try {

			EnvioEmailDicDetalhesDto envioEmailDetalhesDto = new EnvioEmailDicDetalhesDto(idPrograma,
					"",
					"",
					emailsInteressadosList,
					tituloPrograma);

			confirmacaoEnvioEmail = emailService.enviarEmailSolicitandoAssinaturasPrograma(envioEmailDetalhesDto);

			if (confirmacaoEnvioEmail) {
				logger.info(
						"Email aviso para solicitacao de assinaturas enviado com sucesso para o programa id "
								+ idPrograma);
			} else {
				erros.add("Erro ao enviar aviso para solicitacao de assinaturas do programa id " + idPrograma);
			}

		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		} catch (MessagingException e) {
			logger.error(e.getMessage());
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		return true;

	}

	// public Mono<Void> assinarProgramaEdocs(Long idPrograma, String subAssinante)
	// {

	// return Mono.fromCallable(() -> this.buscar(idPrograma))
	// .flatMap(programa -> {
	// validarAssinatura(programa, subAssinante);
	// return integracaoEdocsService
	// .assinarArquivoFaseAssinaturaEdocsServidor(
	// idPrograma,
	// programa.getIdDocumentoCapturadoEdocs())
	// .flatMap(retorno -> marcarProgramaAssinado(idPrograma, subAssinante))
	// .thenReturn(null);
	// });

	// }

	public Mono<Void> assinarProgramaEdocs(Long idPrograma, String subAssinante) {

		return Mono.fromCallable(() -> this.buscar(idPrograma))
				.subscribeOn(Schedulers.boundedElastic()) // JPA aqui
				.flatMap(programa -> {
					validarAssinatura(programa, subAssinante);

					return integracaoEdocsService
							.assinarArquivoFaseAssinaturaEdocsServidor(
									idPrograma,
									programa.getIdDocumentoCapturadoEdocs())
							.flatMap(
									retorno -> Mono.fromRunnable(() -> marcarProgramaAssinado(idPrograma, subAssinante))
											.subscribeOn(Schedulers.boundedElastic()));
				})
				.then();
	}

	private void validarAssinatura(Programa programa, String subAssinante) {

		List<String> erros = new ArrayList<>();

		Set<ProgramaAssinaturaEdocs> assinantesDevemAssinarPrograma = programa.getProgramaAssinantesEdocsSet();

		if (!assinantesDevemAssinarPrograma.stream()
				.anyMatch(assinante -> assinante.getPessoa().getSub().equals(subAssinante))) {
			erros.add(
					"Assinante informado, sub " + subAssinante + ", não faz parte da lista de assinantes do programa.");
		}

		if (assinantesDevemAssinarPrograma.stream()
				.anyMatch(assinante -> assinante.getPessoa().getSub().equals(subAssinante)
						&& assinante.getDataAssinatura() != null)) {
			erros.add("Documento já foi assinado pelo sub " + subAssinante + ".");
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

	}

	@Transactional
	public void marcarProgramaAssinado(Long idPrograma, String subAssinante) {

		Programa programa = repository.findById(idPrograma)
				.orElseThrow(() -> new ValidacaoSiscapException(List.of("Programa não encontrado.")));

		ProgramaAssinaturaEdocs assinatura = programa.getProgramaAssinantesEdocsSet()
				.stream()
				.filter(a -> subAssinante.equals(a.getPessoa().getSub()))
				.findFirst()
				.orElseThrow(() -> new ValidacaoSiscapException(
						List.of("Existe(m) documento(s) a serem assinados.")));

		assinatura.setDataAssinatura(LocalDateTime.now());
		assinatura.setStatusAssinatura(TipoStatusAssinaturaEnum.ASSINADO.getValue());

		programaAssinaturaEdocsRepository.save(assinatura);

	}

	public Mono<Void> autuarProgramaEdocs(Long idPrograma) {

		return Mono.fromCallable(() -> this.buscar(idPrograma))
				.flatMap(programa -> {
					validarSeTodasAssinaturasForamRealizadas(programa);
					return integracaoEdocsService
							.autuarProgramaProjetoReativo(
									idPrograma,
									programa.getIdDocumentoCapturadoEdocs())
							.flatMap(retorno -> {
								String idProcessoEdocs = "";
								String protocoloEdocs = "";
								atualizaDadosProgramaAutuado(idPrograma, idProcessoEdocs, protocoloEdocs);
								return null;
							})
							.thenReturn(null);
				});

	}

	@Transactional
	public Mono<Void> atualizaDadosProgramaAutuado(Long idPrograma, String idProcessoEdocs, String protocoloEdocs) {

		Programa programa = repository.findById(idPrograma)
				.orElseThrow(() -> new ValidacaoSiscapException(Arrays.asList("Programa não encontrado.")));

		programa.setIdProcessoEdocs(idProcessoEdocs);
		programa.setProtocoloEdocs(protocoloEdocs);

		repository.save(programa);

		return Mono.empty();

	}

	private void validarSeTodasAssinaturasForamRealizadas(Programa programa) {

		List<String> erros = new ArrayList<>();

		Set<ProgramaAssinaturaEdocs> assinantesDevemAssinarPrograma = programa.getProgramaAssinantesEdocsSet();

		if (!assinantesDevemAssinarPrograma.stream()
				.anyMatch(assinante -> assinante.getDataAssinatura() == null)) {
			erros.add(
					"Programa com assinatura ainda pendente.");
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

	}

}