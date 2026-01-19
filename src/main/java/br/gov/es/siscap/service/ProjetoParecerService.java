package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoParecerDto;
import br.gov.es.siscap.enums.LotacaoUsuarioEnum;
import br.gov.es.siscap.enums.StatusParecerEnum;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.ProjetoNaoEncontradoException;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoParecer;
import br.gov.es.siscap.repository.ProjetoParecerRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoParecerService {

	@Value("${api.parecer.guidSUBEPP}")
	private String guidSUBEPP;

	@Value("${api.parecer.guidSUBEO}")
	private String guidSUBEO;

	@Value("${api.edocs.guiddestinoSUBCAP}")
	private String guidSUBCAP;

	@Value("${email.destinatario-subcap}")
	private String DESTINO_AVISO_PARECER_CAPTURA;

	@Value("${frontend.host}")
	private String frontEndHost;

	private final ProjetoParecerRepository projetoParecerRepository;
	private final AutenticacaoService autenticacaoService;
	private final UsuarioService usuarioService;
	private final EmailService emailService;

	private final Logger logger = LogManager.getLogger(ProjetoParecer.class);

	public Set<ProjetoParecer> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando pareceres vinculados ao DIC com id: {}", projeto.getId());
		return this.projetoParecerRepository
				.findAllByProjeto(projeto)
				.stream()
				.map(p -> {
					LotacaoUsuarioEnum lotacao = LotacaoUsuarioEnum.fromGuid(
							p.getGuidUnidadeOrganizacao(),
							guidSUBEPP,
							guidSUBEO,
							guidSUBCAP);
					p.setLotacaoParecer(lotacao);
					return p;
				})
				.collect(Collectors.toSet());
	}

	@Transactional
	public ProjetoParecer cadastrar(Projeto projeto, ProjetoParecerDto projetoParecerUsuarioDto) {

		logger.info("Cadastrando pareceres DIC com id: {}", projeto.getId());

		Set<ProjetoParecer> projetoParecerSet = new HashSet<>();

		String subUsuario = autenticacaoService.getUsuarioLogado();
		String guidOrgaoLotacaoUsuario = usuarioService.lotacaoGuidUsuario(subUsuario);

		ProjetoParecer projetoParecer = new ProjetoParecer(projeto, guidOrgaoLotacaoUsuario,
				projetoParecerUsuarioDto.textoParecer(), StatusParecerEnum.PENDENTE);

		projetoParecerSet.add(projetoParecer);

		projetoParecerRepository.saveAllAndFlush(projetoParecerSet);

		logger.info("Parecer referente ao DIC {} cadastrado com sucesso", projeto.getId());

		return projetoParecer;

	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {

		logger.info("Excluindo pareceres por DIC com id: {}", projeto.getId());

		Set<ProjetoParecer> projetoIndicadorSet = this.buscarPorProjeto(projeto);

		projetoParecerRepository.deleteAll(projetoIndicadorSet);

		logger.info(" pareceres vinculados ao DIC excluídos com sucesso");

	}

	@Transactional
	public void excluirFisicamentePorProjeto(Projeto projeto) {

		logger.info("Excluindo fisicamente pareceres registrados do DIC com id: {}", projeto.getId());

		projetoParecerRepository.deleteFisicoPorProjeto(projeto.getId());

		logger.info("Ações do projeto excluidas fisicamente com sucesso");

	}

	@Transactional
	public ProjetoParecer atualizar(Projeto projeto, ProjetoParecerDto projetoParecerDto) {

		if (projetoParecerDto.guidDocumentoEdocs() != null && projetoParecerDto.guidDocumentoEdocs().length() > 0) {
			throw new ValidacaoSiscapException(
					List.of("O parecer já foi enviado e não pode mais ser alterado ou reenviado."));
		}

		logger.info("Alterando dados de um parecer do Projeto com id: {}", projeto.getId());

		String tipoParecer = "";

		if (projetoParecerDto.guidUnidadeOrganizacao().equals(guidSUBEPP))
			tipoParecer = "ESTRATÉGICO";
		else if (projetoParecerDto.guidUnidadeOrganizacao().equals(guidSUBEO))
			tipoParecer = "ORÇAMENTÁRIO";
		else if (projetoParecerDto.guidUnidadeOrganizacao().equals(guidSUBCAP))
			tipoParecer = "GEOC";

		if (projetoParecerDto.id() == null || projetoParecerDto.id() == 0) {
			if (projetoParecerRepository.existsByProjetoIdAndGuidUnidadeOrganizacao(projeto.getId(),
					projetoParecerDto.guidUnidadeOrganizacao())) {
				throw new ValidacaoSiscapException(
						List.of("Já existe para esse projeto parecer vinculado ao setor : " + tipoParecer));
			}
		} else {
			if (projetoParecerDto.guidUnidadeOrganizacao() == null
					|| projetoParecerDto.guidUnidadeOrganizacao().isEmpty()) {
				throw new ValidacaoSiscapException(
						List.of("Setor não informado para atualizacao do parecer."));
			}
		}

		if (projetoParecerDto.textoParecer() == null
				|| projetoParecerDto.textoParecer().isEmpty()) {
			throw new ValidacaoSiscapException(
					List.of("Texto do parecer não informado."));
		}

		Set<ProjetoParecer> ProjetoParecerSet = this.buscarPorProjeto(projeto);

		Set<ProjetoParecer> pareceresProjetoAtualizarSet = this.atualizarPareceresProjeto(projeto, ProjetoParecerSet,
				projetoParecerDto);

		projetoParecerRepository.saveAllAndFlush(pareceresProjetoAtualizarSet);

		logger.info("Ações do projeto alterada com sucesso");

		return this.buscarPorProjeto(projeto)
				.stream()
				.filter(parecer -> parecer.getGuidUnidadeOrganizacao()
						.equals(projetoParecerDto.guidUnidadeOrganizacao()))
				.findFirst()
				.orElse(null);

	}

	private ProjetoParecer buscar(Long id) {
		return projetoParecerRepository.findById(id).orElseThrow(() -> new ProjetoNaoEncontradoException(id));
	}

	public Boolean verificarEnvioPareceresProjeto(Long idProjeto) {

		var pareceres = projetoParecerRepository.findAllByProjetoId(idProjeto);

		boolean subeppEnviado = pareceres.stream()
				.anyMatch(p -> p.getGuidDocumentoEdocs() != null
						&& p.getStatusParecer() == StatusParecerEnum.ENVIADO.getValue()
						&& p.getGuidUnidadeOrganizacao().equals(guidSUBEPP));

		boolean subeoEnviado = pareceres.stream()
				.anyMatch(p -> p.getGuidDocumentoEdocs() != null
						&& p.getStatusParecer() == StatusParecerEnum.ENVIADO.getValue()
						&& p.getGuidUnidadeOrganizacao().equals(guidSUBEO));

		return subeppEnviado && subeoEnviado;

	}

	public Boolean verificarCapturaParecer(Long idParecer) {
		Optional<ProjetoParecer> parecer = projetoParecerRepository.findById(idParecer);
		return parecer
				.map(p -> p.getGuidDocumentoEdocs() != null && p.getGuidDocumentoEdocs().length() > 0)
				.orElse(false);
	}

	public Boolean verificarEntranhamentoParecer(Long idParecer) {
		Optional<ProjetoParecer> parecer = projetoParecerRepository.findById(idParecer);
		return parecer
				.map(p -> p.getStatusParecer() == StatusParecerEnum.ENTRANHADO_EDOCS.getValue())
				.orElse(false);
	}

	public String buscarTipoParecer(Long idParecer) {

		ProjetoParecer projetoParecer = this.buscar(idParecer);
		String tipoParecer = "";

		if (projetoParecer.getGuidUnidadeOrganizacao().equals(guidSUBEPP))
			tipoParecer = "ESTRATÉGICO";
		else if (projetoParecer.getGuidUnidadeOrganizacao().equals(guidSUBEO))
			tipoParecer = "ORÇAMENTÁRIO";
		else if (projetoParecer.getGuidUnidadeOrganizacao().equals(guidSUBCAP))
			tipoParecer = "GEOC";

		return tipoParecer;

	}

	public String gerarNomeArquivoParecerDIC(Long id) {

		ProjetoParecer projetoParecer = this.buscar(id);

		return "PARECER " + this.buscarTipoParecer(id) + " " +
				projetoParecer.getProjeto().getCountAno() + "-" +
				projetoParecer.getProjeto().getOrganizacao().getNomeFantasia();

	}

	private Set<ProjetoParecer> atualizarPareceresProjeto(Projeto projeto,
			Set<ProjetoParecer> pareceresProjetoExistentes, ProjetoParecerDto parecerDto) {

		Set<ProjetoParecer> pareceresAlterarSet = new HashSet<>();
		Set<ProjetoParecer> pareceresAdicionarSet = new HashSet<>();

		pareceresProjetoExistentes
				.stream()
				.filter(projetoParecer -> projetoParecer.compararIdParecerComParecerDto(parecerDto))
				.findFirst()
				.ifPresentOrElse(
						(projetoParecer) -> {
							projetoParecer.atualizarParecer(parecerDto, projeto);
							pareceresAlterarSet.add(projetoParecer);
						},
						() -> {
							String subUsuario = autenticacaoService.getUsuarioLogado();
							String guidOrgaoLotacaoUsuario = usuarioService.lotacaoGuidUsuario(subUsuario);
							pareceresAdicionarSet.add(new ProjetoParecer(projeto, guidOrgaoLotacaoUsuario,
									parecerDto.textoParecer(), StatusParecerEnum.PENDENTE));
						});

		pareceresAdicionarSet.addAll(pareceresAlterarSet);

		return pareceresAdicionarSet;

	}

	@Transactional
	public void atualizarIdArquivoCapturado(String guidArquivoCapturado, Long idParecer, String subUsuarioLogado) {

		ProjetoParecer projetoParecer = this.buscar(idParecer);

		projetoParecer.setGuidDocumentoEdocs(guidArquivoCapturado);
		projetoParecer.setStatusParecer(StatusParecerEnum.ENVIADO.getValue());
		projetoParecer.setDataEnvio(LocalDateTime.now());
		projetoParecer.setSubUsuarioEnviou(subUsuarioLogado);

		projetoParecerRepository.save(projetoParecer);

	}

	@Transactional
	public void atualizarStatusParecer(Long idParecer, StatusParecerEnum statusParecer) {

		ProjetoParecer projetoParecer = this.buscar(idParecer);

		projetoParecer.setStatusParecer(statusParecer.getValue());

		projetoParecerRepository.save(projetoParecer);

	}

	@Transactional
	public boolean enviarAvisoPareceresProjetoCapturadosEdocs(Long idProjeto, String siglaProjeto) {

		List<String> erros = new ArrayList<>();

		boolean confirmacaoEnvioEmail = false;
		List<String> emailsInteressadosList = new ArrayList<String>();
		emailsInteressadosList.add(DESTINO_AVISO_PARECER_CAPTURA);

		try {

			confirmacaoEnvioEmail = emailService.enviarEmailPareceresCapturadosProjeto(emailsInteressadosList,
					idProjeto, siglaProjeto);

			if (confirmacaoEnvioEmail) {
				logger.info(
						"Email aviso captura pareceres do projeto enviado com sucesso para o projeto id " + idProjeto);
			} else {
				erros.add("Erro ao enviar aviso captura pareceres do projeto id " + idProjeto);
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

	public Boolean verificarEnvioParecereGEOCProjeto(Long idProjeto) {

		var pareceres = projetoParecerRepository.findAllByProjetoId(idProjeto);

		return pareceres.stream()
				.anyMatch(p -> p.getGuidDocumentoEdocs() != null
						&& p.getStatusParecer() == StatusParecerEnum.ENVIADO.getValue()
						&& p.getGuidUnidadeOrganizacao().equals(guidSUBCAP));

	}

}