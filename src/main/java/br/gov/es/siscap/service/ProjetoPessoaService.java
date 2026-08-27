package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaOrganizacaoDto;
import br.gov.es.siscap.enums.TipoStatusEnum;
import br.gov.es.siscap.exception.EquipeSemResponsavelProponenteException;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoPessoa;
import br.gov.es.siscap.repository.PessoaRepository;
import br.gov.es.siscap.repository.ProjetoPessoaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoPessoaService {

	// private final MensagemErroRest mensagemErroRest;
	private final ProjetoPessoaRepository projetoPessoaRepository;
	private final PessoaRepository pessoaRepository;
	private final AcessoCidadaoService acessoCidadaoService;
	private final OrganogramaService organogramaService;

	private final Logger logger = LogManager.getLogger(ProjetoPessoaService.class);

	// ProjetoPessoaService(MensagemErroRest mensagemErroRest) {
	// 	this.mensagemErroRest = mensagemErroRest;
	// }

	public Set<ProjetoPessoa> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando equipe do Projeto com id: {}", projeto.getId());
		return projetoPessoaRepository.findAllByProjeto(projeto);
	}

	public Set<ProjetoPessoa> buscarPorPessoa(Pessoa pessoa) {
		logger.info("Buscando equipe da Pessoa com id: {}", pessoa.getId());
		return projetoPessoaRepository.findAllByPessoa(pessoa);
	}

	@Transactional
	public Set<ProjetoPessoa> cadastrar(Projeto projeto, Long idResponsavelProponente, List<EquipeDto> equipeDtoList) {

		logger.info("Cadastrando equipe do Projeto com id: {}", projeto.getId());

		if (idResponsavelProponente == null) {
			throw new EquipeSemResponsavelProponenteException();
		}

		Set<ProjetoPessoa> projetoPessoaSet = new HashSet<>();

		String subResponsavelProponente = pessoaRepository.findById(idResponsavelProponente)
				.map(Pessoa::getSub)
				.orElse(null);

		String entidadeResponsavelProponente = buscarEntidadePorSubPessoa(subResponsavelProponente);

		ProjetoPessoa responsavelProponente = new ProjetoPessoa(projeto, idResponsavelProponente,
				entidadeResponsavelProponente);

		responsavelProponente.getPessoa().setSub(subResponsavelProponente);

		projetoPessoaSet.add(responsavelProponente);

		equipeDtoList.forEach(equipeDto -> {

			String entidade = buscarEntidadePorSubPessoa(equipeDto.subPessoa());

			ProjetoPessoa projetoPessoa = new ProjetoPessoa(projeto, equipeDto, entidade);

			projetoPessoa.getPessoa().setSub(equipeDto.subPessoa());

			projetoPessoaSet.add(projetoPessoa);

		});

		List<ProjetoPessoa> projetoPessoaList = projetoPessoaRepository.saveAll(projetoPessoaSet);

		logger.info("Equipe do projeto cadastrada com sucesso");

		return new HashSet<>(projetoPessoaList);
	}

	@Transactional
	public Set<ProjetoPessoa> atualizar(Projeto projeto, Long idResponsavelProponente, List<EquipeDto> equipeDtoList) {

		logger.info("Alterando dados da equipe do Projeto com id: {}", projeto.getId());

		Set<ProjetoPessoa> projetoPessoaSet = this.buscarPorProjeto(projeto);

		ProjetoPessoa responsavelProponente = this.buscarResponsavelProponente(projetoPessoaSet);

		if (!this.compararIdsResponsavelProponente(responsavelProponente.getPessoa().getId(),
				idResponsavelProponente)) {

			String entidadeResponsavelProponente = buscarEntidadePorSubPessoa(
					responsavelProponente.getPessoa().getSub());

			responsavelProponente.atualizarResponsavelProponente(TipoStatusEnum.INATIVO.getValue());

			projetoPessoaRepository.save(responsavelProponente);
			projetoPessoaRepository.save(new ProjetoPessoa(projeto, idResponsavelProponente, entidadeResponsavelProponente));

		}

		Set<ProjetoPessoa> membrosEquipeSet = this.buscarMembrosEquipe(projetoPessoaSet);

		Set<ProjetoPessoa> membrosEquipeAtualizarSet = this.atualizarMembrosEquipe(projeto, membrosEquipeSet,
				equipeDtoList);

		if (!membrosEquipeAtualizarSet.isEmpty()) {
			projetoPessoaRepository.saveAllAndFlush(membrosEquipeAtualizarSet);
		}

		logger.info("Equipe do projeto alterada com sucesso");

		return this.buscarPorProjeto(projeto);

	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {
		logger.info("Excluindo equipe do Projeto com id: {}", projeto.getId());

		Set<ProjetoPessoa> projetoPessoaSet = this.buscarPorProjeto(projeto);

		projetoPessoaSet.forEach(projetoPessoa -> projetoPessoa.apagar("Projeto excluido"));

		List<ProjetoPessoa> projetoPessoaList = projetoPessoaRepository.saveAllAndFlush(projetoPessoaSet);

		projetoPessoaRepository.deleteAll(projetoPessoaList);

		logger.info("Equipe do projeto excluida com sucesso");
	}

	@Transactional
	public void excluirFisicamentePorProjeto(Projeto projeto) {
		logger.info("Excluindo fisicamente equipe do Projeto com id: {}", projeto.getId());

		projetoPessoaRepository.deleteFisicoPorProjeto(projeto.getId());

		logger.info("Equipe do projeto excluida fisicamente com sucesso");
	}

	@Transactional
	public void excluirPorPessoa(Pessoa pessoa) {
		logger.info("Excluindo Pessoa com id: {} da(s) equipe(s) de projeto", pessoa.getId());

		Set<ProjetoPessoa> projetoPessoaSet = this.buscarPorPessoa(pessoa);

		projetoPessoaSet.forEach(projetoPessoa -> projetoPessoa.apagar("Pessoa excluida"));

		List<ProjetoPessoa> projetoPessoaList = projetoPessoaRepository.saveAllAndFlush(projetoPessoaSet);

		projetoPessoaRepository.deleteAll(projetoPessoaList);

		logger.info("Pessoa excluida da(s) equipe(s) de projeto com sucesso");
	}

	private ProjetoPessoa buscarResponsavelProponente(Set<ProjetoPessoa> projetoPessoaSet) {
		projetoPessoaSet.stream().forEach(ps -> logger.info("Tipo papel busca responsavel : {} ", ps.getTipoPapel()));
		return projetoPessoaSet
				.stream()
				.filter(ProjetoPessoa::isResponsavelProponente)
				.findFirst()
				.orElseThrow(EquipeSemResponsavelProponenteException::new);
	}

	private Set<ProjetoPessoa> buscarMembrosEquipe(Set<ProjetoPessoa> projetoPessoaSet) {

		return projetoPessoaSet
				.stream()
				.filter(Predicate.not(ProjetoPessoa::isResponsavelProponente))
				.collect(Collectors.toSet());
	}

	private boolean compararIdsResponsavelProponente(Long id_A, Long id_B) {
		return Objects.equals(id_A, id_B);
	}

	private Set<ProjetoPessoa> atualizarMembrosEquipe(Projeto projeto, Set<ProjetoPessoa> membrosEquipeSet,
			List<EquipeDto> equipeDtoList) {

		Set<ProjetoPessoa> membrosEquipeAlterarSet = new HashSet<>();
		Set<ProjetoPessoa> membrosEquipeAdicionarSet = new HashSet<>();

		equipeDtoList.forEach(equipeDto -> {
			membrosEquipeSet
					.stream()
					.filter(projetoPessoa -> projetoPessoa.compararIdPessoaComEquipeDto(equipeDto))
					.findFirst()
					.ifPresentOrElse(
							(projetoPessoa) -> {
								if (equipeDto.idStatus() != null
										&& equipeDto.idStatus().equals(TipoStatusEnum.EXCLUIDO.getValue())) {
									projetoPessoaRepository.deleteFisico(projetoPessoa.getId()); // excluir fisicamente
																									// da tabela
								} else {
									projetoPessoa.atualizarMembroEquipe(equipeDto);
									membrosEquipeAlterarSet.add(projetoPessoa);
								}
							},
							() -> {

								String entidade = buscarEntidadePorSubPessoa(equipeDto.subPessoa());

								membrosEquipeAdicionarSet.add(new ProjetoPessoa(projeto, equipeDto, entidade));

							});
		});

		membrosEquipeAdicionarSet.addAll(membrosEquipeAlterarSet);

		return membrosEquipeAdicionarSet;

	}

	private String buscarEntidadePorSubPessoa(String subPessoa) {

		List<ACAgentePublicoPapelDto> listaPapeisUsuario = acessoCidadaoService
				.listarPapeisAgentePublicoPorSub(subPessoa);

		String guidLotacaoPapelUsuario = listaPapeisUsuario.stream()
				.filter(papel -> Boolean.TRUE.equals(papel.Prioritario()))
				.findFirst()
				.or(() -> listaPapeisUsuario.stream().findFirst())
				.map(ACAgentePublicoPapelDto::LotacaoGuid)
				.orElse(null);

		if (guidLotacaoPapelUsuario == null || guidLotacaoPapelUsuario.isBlank()) {
			return "";
		}

		String guidOrganizacao = organogramaService
				.listarUnidadeInfoPorLotacaoGuid(guidLotacaoPapelUsuario)
				.guidOrganizacao();

		if (guidOrganizacao == null || guidOrganizacao.isBlank()) {
			return "";
		}

		OrganogramaOrganizacaoDto organizacaoDto = organogramaService
				.listarDadosOrganizacaoPorGuid(guidOrganizacao);

		if (organizacaoDto == null) {
			return "";
		}

		return organizacaoDto.sigla() + " - " + organizacaoDto.nomeFantasia();

	}

}