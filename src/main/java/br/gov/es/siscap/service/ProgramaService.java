package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProgramaAssinaturaEdocsDto;
import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.ProgramaOrganizacaoDto;
import br.gov.es.siscap.dto.listagem.ProgramaListaDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.enums.StatusProgramaEnum;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.form.ProgramaForm;
import br.gov.es.siscap.models.LocalidadeQuantia;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaAssinaturaEdocs;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProgramaRepository;
import br.gov.es.siscap.utils.FormatadorCountAno;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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
	private final ProgramaOrganizacaoService programaOrganizacaoService;
	private final Logger logger = LogManager.getLogger(ProgramaService.class);

	@Value("${api.programa.assinantes.gestorSUBCAP}")
	private String assinanteEdocsProgramaGestorSUBCAP;

	@Value("${api.programa.assinantes.gestorSEP}")
	private String assinanteEdocsProgramaGestorSEP;

	@Value("${api.programa.assinantes.gestorGOVES}")
	private String assinanteEdocsProgramaGestorGOVES;

	public Page<ProgramaListaDto> listarTodos(Pageable pageable, String search) {
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

		List<ProgramaAssinaturaEdocsDto> assinantesProgramaListDto = programaAssinaturaEdocsService
				.buscarPorPrograma(programa);

		List<ProgramaOrganizacaoDto> programaOrganizacaoDtos = programaOrganizacaoService.buscarPorPrograma(programa);

		return new ProgramaDto(programa, equipeCaptacao, idProjetoPropostoList, assinantesProgramaListDto,
				programaOrganizacaoDtos);

	}

	@Transactional
	public ProgramaDto cadastrar(ProgramaForm form) {

		logger.info("Cadastrando novo programa");
		logger.info("Dados: {}", form);

		this.validarProgramaForm(form);

		Programa tempPrograma = new Programa(form);

		tempPrograma.setStatus(StatusProgramaEnum.EDICAO.getValue());

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

		List<ProgramaOrganizacaoDto> organizacoesProgramaGravar = form.orgaosEnvolvidosList();

		List<ProgramaOrganizacaoDto> organizacoesPrograma = programaOrganizacaoService.cadastrar(programa,
				organizacoesProgramaGravar);

		logger.info("Programa cadastrado com sucesso");

		return new ProgramaDto(programa, equipeCaptacao, idProjetoPropostoList, null, organizacoesPrograma);

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

		this.validarProgramaForm(form);

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

		List<ProgramaOrganizacaoDto> organizacoesProgramaAtualizar = form.orgaosEnvolvidosList();

		List<ProgramaOrganizacaoDto> organizacoesProgramaList = programaOrganizacaoService.atualizar(programa,
				organizacoesProgramaAtualizar);

		logger.info("Programa atualizado com sucesso");

		return new ProgramaDto(programaResult, equipeCaptacao, idProjetoPropostoList, null, organizacoesProgramaList);

	}

	@Transactional
	public void excluir(Long id) {

		logger.info("Excluindo programa com id: {}", id);

		Programa programa = this.buscar(id);

		if (!StringUtils.isBlank(programa.getProtocoloEdocs())) {
			throw new ValidacaoSiscapException(
					List.of("Programa não pode ser excluído pois já possui um protocolo E-Docs associado."));
		}

		programa.apagar();
		repository.saveAndFlush(programa);

		programaPessoaService.excluirPorPrograma(programa);
		projetoService.desvincularProjetosDoPrograma(programa);

		logger.info("Programa excluído com sucesso");

	}

	public Integer buscarQuantidadeProgramas() {
		return Integer.parseInt(String.valueOf((repository.count())));
	}

	private Programa buscar(long id) {
		return repository.findById(id).orElseThrow(() -> {
			throw new ValidacaoSiscapException(List.of("Programa não encontrado"));
		});
	}

	private String buscarCountAnoFormatado() {
		return FormatadorCountAno.formatar(repository.contagemAnoAtual());
	}

	public String gerarNomeArquivo(Long idPrograma) {

		Programa programa = this.buscar(idPrograma.longValue());

		return "PROGRAMA n. " +
				programa.getCountAno();
	}

	public void criarArquivoProgramaEdocsAssinaturasPendentes(Long idPrograma) {
		this.validarAssinaturasSolicitadas(idPrograma);
		String nomeArquivo = this.gerarNomeArquivo(idPrograma);
		List<String> subAssinantesEdocsPrograma = List.of(assinanteEdocsProgramaGestorSUBCAP,
				assinanteEdocsProgramaGestorSEP, assinanteEdocsProgramaGestorGOVES);
		asyncExecutorService.criarArquivoProgramaFaseAssinaturaEdocsServidor(idPrograma, subAssinantesEdocsPrograma,
				nomeArquivo);
	}

	public void assinarProgramaEdocs(Long idPrograma, String subAssinante) {
		Programa programa = this.buscar(idPrograma);
		this.validarAssinatura(programa, subAssinante);
		String idDocumentoCapturadoEdocs = programa.getIdDocumentoCapturadoEdocs();
		asyncExecutorService.assinarArquivoFaseAssinaturaEdocsServidor(idPrograma, idDocumentoCapturadoEdocs,
				subAssinante);
	}

	private void validarAssinatura(Programa programa, String subAssinante) {

		List<String> erros = new ArrayList<>();

		Set<ProgramaAssinaturaEdocs> assinantesDevemAssinarPrograma = programa.getProgramaAssinantesEdocsSet();

		if (assinantesDevemAssinarPrograma == null) {
			erros.add(
					"Nenhum ssinante encontrado para o programa id " + programa.getId() + ".");
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		if (!assinantesDevemAssinarPrograma.stream()
				.anyMatch(assinante -> assinante.getPessoa().getSub().equals(subAssinante.trim().toLowerCase()))) {
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

	private void validarAssinaturasSolicitadas(long idPrograma) {

		List<String> erros = new ArrayList<>();

		Programa programa = this.buscar(idPrograma);

		Set<ProgramaAssinaturaEdocs> assinantesDevemAssinarPrograma = programa.getProgramaAssinantesEdocsSet();

		if (!assinantesDevemAssinarPrograma.isEmpty()) {
			erros.add(
					"Assinaturas já solicitadas para o programa id " + programa.getId() + ".");
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

	}

	public void autuarProgramaEdocs(Long idPrograma) {
		Programa programa = this.buscar(idPrograma);
		this.validarSeProgramaPodeSerAutuado(programa);
		ProgramaDto programaDto = this.buscarPorId(idPrograma);
		asyncExecutorService.autuarProgramaEdocs(programaDto);
	}

	private void validarSeTodasAssinaturasForamRealizadas(Programa programa) {

		List<String> erros = new ArrayList<>();

		Set<ProgramaAssinaturaEdocs> assinantesDevemAssinarPrograma = programa.getProgramaAssinantesEdocsSet();

		if (assinantesDevemAssinarPrograma.stream()
				.anyMatch(assinante -> assinante.getDataAssinatura() == null)) {
			erros.add(
					"Programa com assinatura ainda pendente.");
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

	}

	private void validarSeProgramaPodeSerAutuado(Programa programa) {

		List<String> erros = new ArrayList<>();

		if(programa.getStatus().equals(StatusProgramaEnum.RECUSADO.getValue()))
			erros.add(
				"Programa não pode ser autuado pois está recusado.");

		String protocoloEdocs = programa.getProtocoloEdocs();
		if (protocoloEdocs != null && !protocoloEdocs.isBlank()) {
			erros.add(
					"Programa já foi autuado sobre o protocolo " + programa.getProtocoloEdocs() + ".");
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		this.validarSeTodasAssinaturasForamRealizadas(programa);

	}

	private void validarProgramaForm(ProgramaForm form) {

		if (form.valorCalculadoTotal() == null || form.valorCalculadoTotal().compareTo(BigDecimal.ZERO) == 0) {
			throw new ValidacaoSiscapException(List.of("Valor total estimado do programa não pode ser zerado."));
		}

		BigDecimal totalEstimadoDicsPrograma = form.idProjetoPropostoList()
				.stream()
				.map(dicId -> {
					Projeto projeto = projetoService.buscar(dicId);
					return projeto.getLocalidadeQuantiaSet().stream().map(LocalidadeQuantia::getQuantia)
							.reduce(BigDecimal.ZERO, BigDecimal::add);
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		if (form.percentualCustoAdministrativo() != null
				&& form.percentualCustoAdministrativo().compareTo(BigDecimal.ZERO) > 0) {
			totalEstimadoDicsPrograma = totalEstimadoDicsPrograma
					.multiply(form.percentualCustoAdministrativo()
							.divide(BigDecimal.valueOf(100)).add(BigDecimal.valueOf(1)));
		}

		if (totalEstimadoDicsPrograma == null || totalEstimadoDicsPrograma.compareTo(form.valorCalculadoTotal()) != 0) {
			throw new ValidacaoSiscapException(List.of(
					"Valor total estimado do programa está inválido, ele deve ser o resultado dos valores somados dos DIC´s mais o percentual de custo administrativo se houver."));
		}

	}

	@Transactional
	public void recusarAssinaturaProgramaEdocs(Long idPrograma, String subAssinante) {
		Programa programa = this.buscar(idPrograma);
		String idDocumentoCapturadoEdocs = programa.getIdDocumentoCapturadoEdocs();
		asyncExecutorService.recusarAssinaturaProgramaEdocs(idPrograma, idDocumentoCapturadoEdocs, subAssinante);
	}

}