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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.support.TransactionSynchronization;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	private String destinoAvisoParecerCaptura;

	@Value("${frontend.host}")
	private String frontEndHost;

	@Value("${api.parecer.anexos_pdf}")
	private String uploadPathStr;

	private final ProjetoParecerRepository projetoParecerRepository;
	private final AutenticacaoService autenticacaoService;
	private final UsuarioService usuarioService;
	private final EmailService emailService;

	private final Logger logger = LogManager.getLogger(ProjetoParecerService.class);

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
	public ProjetoParecer cadastrar(Projeto projeto, ProjetoParecerDto projetoParecerUsuarioDto,
			MultipartFile arquivoParecerAnexo) {

		logger.info("Cadastrando pareceres DIC com id: {}", projeto.getId());

		Set<ProjetoParecer> projetoParecerSet = new HashSet<>();

		String subUsuario = autenticacaoService.getUsuarioLogado();
		String guidOrgaoLotacaoUsuario = usuarioService.lotacaoGuidUsuario(subUsuario);

		ProjetoParecer projetoParecer = new ProjetoParecer(projeto,
				guidOrgaoLotacaoUsuario,
				projetoParecerUsuarioDto.textoParecer(),
				StatusParecerEnum.PENDENTE,
				projetoParecerUsuarioDto.nomeArquivo(),
				projetoParecerUsuarioDto.nomeOriginalArquivo());

		boolean semTexto = projetoParecerUsuarioDto.textoParecer() == null
				|| projetoParecerUsuarioDto.textoParecer().trim().isEmpty();

		boolean semArquivo = projetoParecerUsuarioDto.nomeArquivo() == null
				|| projetoParecerUsuarioDto.nomeArquivo().isEmpty();

		if (semTexto && semArquivo) {
			throw new ValidacaoSiscapException(
					List.of("Informe o texto do parecer ou anexe um arquivo PDF."));
		}

		try {
			if (!semArquivo && arquivoParecerAnexo != null && !isPdf(arquivoParecerAnexo)) {
				throw new ValidacaoSiscapException(
						List.of("O arquivo anexado deve estar no formato PDF."));
			}
		} catch (IOException e) {
			logger.error(e);
		}

		if (!semArquivo) {
			try {
				projetoParecer.handleFileUpload(arquivoParecerAnexo, projetoParecer, uploadPathStr);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

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
	public ProjetoParecer atualizar(Projeto projeto, ProjetoParecerDto projetoParecerDto,
			MultipartFile arquivoParecerAnexo) {

		if (projetoParecerDto.guidDocumentoEdocs() != null && !projetoParecerDto.guidDocumentoEdocs().isEmpty()) {
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
			tipoParecer = "CAPTAÇÃO";

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

		boolean semTexto = projetoParecerDto.textoParecer() == null
				|| projetoParecerDto.textoParecer().trim().isEmpty();

		boolean semArquivo = projetoParecerDto.nomeArquivo() == null
				|| projetoParecerDto.nomeArquivo().isEmpty();

		if (semTexto && semArquivo) {
			throw new ValidacaoSiscapException(
					List.of("Informe o texto do parecer ou anexe um arquivo PDF."));
		}

		try {
			if (!semArquivo && arquivoParecerAnexo != null && !isPdf(arquivoParecerAnexo)) {
				throw new ValidacaoSiscapException(
						List.of("O arquivo anexado deve estar no formato PDF."));
			}
		} catch (IOException e) {
			logger.error(e);
		}

		Set<ProjetoParecer> projetoParecerSet = this.buscarPorProjeto(projeto);

		Set<ProjetoParecer> pareceresProjetoAtualizarSet = this.atualizarPareceresProjeto(projeto, projetoParecerSet,
				projetoParecerDto, arquivoParecerAnexo);

		if (pareceresProjetoAtualizarSet.isEmpty()) {
			throw new ValidacaoSiscapException(
					List.of("Não foi possível processar os pareceres do projeto."));
		}

		projetoParecerRepository.saveAllAndFlush(pareceresProjetoAtualizarSet);

		logger.info("Pareceres do projeto alterada com sucesso");

		return this.buscarPorProjeto(projeto)
				.stream()
				.filter(parecer -> parecer.getGuidUnidadeOrganizacao()
						.equals(projetoParecerDto.guidUnidadeOrganizacao()))
				.findFirst()
				.orElse(null);

	}

	private boolean isPdf(MultipartFile arquivo) throws IOException {

		try (InputStream is = arquivo.getInputStream()) {

			byte[] header = new byte[5];

			if (is.read(header) < 5) {
				return false;
			}

			return header[0] == '%'
					&& header[1] == 'P'
					&& header[2] == 'D'
					&& header[3] == 'F'
					&& header[4] == '-';
		}

	}

	public ProjetoParecer buscar(Long id) {
		return projetoParecerRepository.findById(id).orElseThrow(() -> new ProjetoNaoEncontradoException(id));
	}

	public boolean verificarEnvioPareceresProjeto(Long idProjeto) {

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

	public boolean verificarCapturaParecer(long idParecer) {
		Optional<ProjetoParecer> parecer = projetoParecerRepository.findById(idParecer);
		return parecer
				.map(p -> p.getGuidDocumentoEdocs() != null && !p.getGuidDocumentoEdocs().isEmpty())
				.orElse(false);
	}

	public boolean verificarEntranhamentoParecer(long idParecer) {
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
			tipoParecer = "CAPTAÇÃO";

		return tipoParecer;

	}

	public String gerarNomeArquivoParecerDIC(Long id) {

		ProjetoParecer projetoParecer = this.buscar(id);

		return "PARECER " + this.buscarTipoParecer(id) + " " +
				projetoParecer.getProjeto().getCountAno() + "-" +
				projetoParecer.getProjeto().getOrganizacao().getNomeFantasia();

	}

	private Set<ProjetoParecer> atualizarPareceresProjeto(Projeto projeto,
			Set<ProjetoParecer> pareceresProjetoExistentes, ProjetoParecerDto parecerDto,
			MultipartFile arquivoParecerAnexo) {

		Set<ProjetoParecer> pareceresAlterarSet = new HashSet<>();
		Set<ProjetoParecer> pareceresAdicionarSet = new HashSet<>();

		pareceresProjetoExistentes
				.stream()
				.filter(projetoParecer -> projetoParecer.compararIdParecerComParecerDto(parecerDto))
				.findFirst()
				.ifPresentOrElse(
						(projetoParecer) -> {
							try {
								projetoParecer.atualizarParecer(parecerDto, projeto, arquivoParecerAnexo,
										uploadPathStr);
							} catch (Exception e) {
								logger.error(e.getMessage());
							}
							pareceresAlterarSet.add(projetoParecer);
						},
						() -> {
							String subUsuario = autenticacaoService.getUsuarioLogado();
							String guidOrgaoLotacaoUsuario = usuarioService.lotacaoGuidUsuario(subUsuario);
							pareceresAdicionarSet.add(new ProjetoParecer(projeto,
									guidOrgaoLotacaoUsuario,
									parecerDto.textoParecer(),
									StatusParecerEnum.PENDENTE,
									parecerDto.nomeArquivo(),
									parecerDto.nomeOriginalArquivo()));
						});

		pareceresAdicionarSet.addAll(pareceresAlterarSet);

		return pareceresAdicionarSet;

	}

	@Transactional
	public void atualizarIdArquivoCapturado(String guidArquivoCapturado, Long idParecer, String subUsuarioLogado,
			String codigoRegistroEdocs) {

		ProjetoParecer projetoParecer = this.buscar(idParecer);

		projetoParecer.setGuidDocumentoEdocs(guidArquivoCapturado);
		projetoParecer.setStatusParecer(StatusParecerEnum.ENVIADO.getValue());
		projetoParecer.setDataEnvio(LocalDateTime.now());
		projetoParecer.setSubUsuarioEnviou(subUsuarioLogado);
		projetoParecer.setRegistroArquivoEdocs(codigoRegistroEdocs);

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
		List<String> emailsInteressadosList = new ArrayList<>();
		emailsInteressadosList.add(destinoAvisoParecerCaptura);

		try {

			confirmacaoEnvioEmail = emailService.enviarEmailPareceresCapturadosProjeto(emailsInteressadosList,
					idProjeto, siglaProjeto);

			if (confirmacaoEnvioEmail) {
				logger.info(
						"Email aviso captura pareceres do projeto enviado com sucesso para o projeto id {}", idProjeto);
			} else {
				erros.add("Erro ao enviar aviso captura pareceres do projeto id " + idProjeto);
			}

		} catch (UnsupportedEncodingException | MessagingException e) {
			logger.error(e.getMessage());
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		return true;

	}

	public boolean verificarEnvioParecereGEOCProjeto(Long idProjeto) {

		var pareceres = projetoParecerRepository.findAllByProjetoId(idProjeto);

		return pareceres.stream()
				.anyMatch(p -> p.getGuidDocumentoEdocs() != null
						&& p.getStatusParecer() == StatusParecerEnum.ENVIADO.getValue()
						&& p.getGuidUnidadeOrganizacao().equals(guidSUBCAP));

	}

	public Resource buscarArquivo(Long idParecer) {

		if (idParecer == null) {
			throw new ValidacaoSiscapException(
					List.of("Id do parecer não informado."));
		}

		ProjetoParecer parecer = projetoParecerRepository.findById(idParecer)
				.orElseThrow(() -> new ValidacaoSiscapException(
						List.of("Parecer não encontrado.")));

		String nomeArquivoSalvo = parecer.getNomeArquivo();

		if (nomeArquivoSalvo == null || nomeArquivoSalvo.isBlank()) {
			throw new ValidacaoSiscapException(
					List.of("Parecer não possui arquivo anexado."));
		}

		try {

			Path diretorioBase = Paths.get(uploadPathStr)
					.toAbsolutePath()
					.normalize();

			Path caminhoArquivo = diretorioBase
					.resolve(nomeArquivoSalvo)
					.normalize();

			if (!caminhoArquivo.startsWith(diretorioBase)) {
				throw new ValidacaoSiscapException(
						List.of("Caminho do arquivo inválido."));
			}

			Resource resource = new UrlResource(caminhoArquivo.toUri());

			if (!resource.exists() || !resource.isReadable()) {
				throw new ValidacaoSiscapException(
						List.of("Arquivo do parecer não encontrado no servidor."));
			}

			return resource;

		} catch (MalformedURLException e) {
			throw new ValidacaoSiscapException(
					List.of("Erro ao localizar o arquivo do parecer."));
		}

	}

	@Transactional
	public void removerAnexoParecer(Long idParecer) {

		if (idParecer == null) {
			throw new ValidacaoSiscapException(
					List.of("Id do parecer não informado."));
		}

		ProjetoParecer parecer = projetoParecerRepository.findById(idParecer)
				.orElseThrow(() -> new ValidacaoSiscapException(
						List.of("Parecer não encontrado.")));

		String nomeArquivoSalvo = parecer.getNomeArquivo();

		if (nomeArquivoSalvo == null || nomeArquivoSalvo.isBlank()) {
			throw new ValidacaoSiscapException(
					List.of("Parecer não possui arquivo anexado."));
		}

		Path diretorioBase = Paths.get(uploadPathStr)
				.toAbsolutePath()
				.normalize();

		Path caminhoArquivo = diretorioBase
				.resolve(nomeArquivoSalvo)
				.normalize();

		if (!caminhoArquivo.startsWith(diretorioBase)) {
			throw new ValidacaoSiscapException(
					List.of("Caminho do arquivo inválido."));
		}

		parecer.setNomeArquivo(null);
		parecer.setTextoParecer(null);
		parecer.setNomeOriginalArquivo(null);

		projetoParecerRepository.save(parecer);

		TransactionSynchronizationManager.registerSynchronization(
				new TransactionSynchronization() {
					@Override
					public void afterCommit() {
						excluirArquivoFisico(nomeArquivoSalvo);
					}
				});

	}

	private void excluirArquivoFisico(String nomeArquivo) {

		Path diretorio = Paths.get(uploadPathStr).toAbsolutePath().normalize();
		Path arquivo = diretorio.resolve(nomeArquivo).normalize();

		if (!arquivo.startsWith(diretorio)) {
			logger.warn("Tentativa de exclusão fora do diretório permitido: {}", arquivo);
			return;
		}

		try {
			boolean excluido = Files.deleteIfExists(arquivo);

			if (!excluido) {
				logger.warn("Arquivo do parecer não encontrado para exclusão: {}", arquivo);
			}
		} catch (IOException e) {
			logger.error("Erro ao excluir arquivo físico do parecer: {}", arquivo, e);
		}
	}

}