package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoParecerDto;
import br.gov.es.siscap.enums.StatusParecerEnum;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.ProjetoNaoEncontradoException;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoParecer;
import br.gov.es.siscap.repository.ProjetoParecerRepository;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoParecerService {

	@Value("${api.parecer.guidSUBEPP}")
	private String guidSUBEPP;

	@Value("${api.parecer.guidSUBEO}")
	private String guidSUBEO;

	private final ProjetoParecerRepository projetoParecerRepository;
	private final AutenticacaoService autenticacaoService;
	private final UsuarioService usuarioService;

	private final Logger logger = LogManager.getLogger(ProjetoParecer.class);

	public Set<ProjetoParecer> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando pareceres vinculados ao DIC com id: {}", projeto.getId());
		return this.projetoParecerRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoParecer> cadastrar(Projeto projeto, List<ProjetoParecerDto> projetoPareceresDtoList) {

		logger.info("Cadastrando pareceres DIC com id: {}", projeto.getId());

		Set<ProjetoParecer> ProjetoComplementosSet = new HashSet<>();

		projetoPareceresDtoList.forEach(parecerDto -> {
			ProjetoParecer complementoProjeto = new ProjetoParecer(projeto, parecerDto);
			ProjetoComplementosSet.add(complementoProjeto);
		});

		List<ProjetoParecer> ProjetoComplementoList = projetoParecerRepository.saveAll(ProjetoComplementosSet);

		logger.info("Campos a serem complementados para o DIC cadastrada com sucesso");

		return new HashSet<>(ProjetoComplementoList);

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
	public ProjetoParecer atualizar(Projeto projeto, ProjetoParecerDto ProjetoParecerDto, boolean isSalvar) {

		logger.info("Alterando dados de um parecer do Projeto com id: {}", projeto.getId());

		String lotacaoParecer = "";

		if (ProjetoParecerDto.guidUnidadeOrganizacao().equals(guidSUBEPP))
			lotacaoParecer = "ESTRATÉGICO";
		else if (ProjetoParecerDto.guidUnidadeOrganizacao().equals(guidSUBEO))
			lotacaoParecer = "ORÇAMENTÁRIO";

		if (ProjetoParecerDto.id() == null || ProjetoParecerDto.id() == 0) {
			if (projetoParecerRepository.existsByProjetoIdAndGuidUnidadeOrganizacao(projeto.getId(),
					ProjetoParecerDto.guidUnidadeOrganizacao())) {
				throw new ValidacaoSiscapException(
						List.of("Já existe para esse projeto parecer vinculado ao setor : " + lotacaoParecer));
			}
		} else {
			if (ProjetoParecerDto.guidUnidadeOrganizacao() == null
					|| ProjetoParecerDto.guidUnidadeOrganizacao().isEmpty()) {
				throw new ValidacaoSiscapException(
						List.of("Setor não informado para atualizacao do parecer."));
			}
		}

		if (ProjetoParecerDto.textoParecer() == null
				|| ProjetoParecerDto.textoParecer().isEmpty()) {
			throw new ValidacaoSiscapException(
					List.of("Texto do parecer não informado."));
		}

		Set<ProjetoParecer> ProjetoParecerSet = this.buscarPorProjeto(projeto);

		Set<ProjetoParecer> pareceresProjetoAtualizarSet = this.atualizarPareceresProjeto(projeto, ProjetoParecerSet,
				ProjetoParecerDto);

		projetoParecerRepository.saveAllAndFlush(pareceresProjetoAtualizarSet);

		logger.info("Ações do projeto alterada com sucesso");

		return this.buscarPorProjeto(projeto)
				.stream()
				.filter(parecer -> parecer.getGuidUnidadeOrganizacao()
						.equals(ProjetoParecerDto.guidUnidadeOrganizacao()))
				.findFirst()
				.orElse(null);

	}

	public ProjetoParecerDto buscarPorId(Long id) {

		logger.info("Buscando parecer de um projeto com id: {}", id);

		ProjetoParecer projetoParecer = this.buscar(id);

		return new ProjetoParecerDto(projetoParecer);

	}

	private ProjetoParecer buscar(Long id) {
		return projetoParecerRepository.findById(id).orElseThrow(() -> new ProjetoNaoEncontradoException(id));
	}

	public String gerarNomeArquivoParecerDIC(Long id) {

		ProjetoParecer projetoParecer = this.buscar(id);
		String tipoParecer = "";

		if (projetoParecer.getGuidUnidadeOrganizacao().equals(guidSUBEPP))
			tipoParecer = "ESTRATÉGICO";
		else if (projetoParecer.getGuidUnidadeOrganizacao().equals(guidSUBEO))
			tipoParecer = "ORÇAMENTÁRIO";

		return "PARECER " + tipoParecer +
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

}