package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.RateioCidadeDto;
import br.gov.es.siscap.dto.RateioDto;
import br.gov.es.siscap.dto.RateioMicrorregiaoDto;
import br.gov.es.siscap.models.Microrregiao;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoCidade;
import br.gov.es.siscap.models.ProjetoMicrorregiao;
import br.gov.es.siscap.repository.ProjetoCidadeRepository;
import br.gov.es.siscap.repository.ProjetoMicrorregiaoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoRateioService {

	private final ProjetoMicrorregiaoRepository projetoMicrorregiaoRepository;
	private final ProjetoCidadeRepository projetoCidadeRepository;
	private final Logger logger = LogManager.getLogger(ProjetoRateioService.class);

	public RateioDto buscarPorProjeto(Projeto projeto) {

		List<RateioMicrorregiaoDto> rateioMicrorregiaoDtoList = this.buscarProjetoMicrorregiaoSetPorProjeto(projeto).stream()
					.map(RateioMicrorregiaoDto::new)
					.toList();

		List<RateioCidadeDto> rateioCidadeDtoList = this.buscarProjetoCidadeSetPorProjeto(projeto).stream()
					.map(RateioCidadeDto::new)
					.toList();

		return new RateioDto(rateioMicrorregiaoDtoList, rateioCidadeDtoList);
	}

	public List<String> listarNomesMicrorregioesRateio(Projeto projeto) {
		return projetoMicrorregiaoRepository
					.findAllByProjeto(projeto)
					.stream()
					.map(ProjetoMicrorregiao::getMicrorregiao)
					.map(Microrregiao::getNome)
					.toList();
	}

	@Transactional
	public RateioDto salvar(Projeto projeto, RateioDto rateioDto) {
		logger.info("Cadastrando novo rateio para o Projeto com id: {}", projeto.getId());

		List<RateioMicrorregiaoDto> rateioMicrorregiaoDtoList = this.salvarRateioMicrorregiao(projeto, rateioDto.rateioMicrorregiao());

		List<RateioCidadeDto> rateioCidadeDtoList = this.salvarRateioCidade(projeto, rateioDto.rateioCidade());

		logger.info("Rateio para o Projeto cadastrado com sucesso");
		return new RateioDto(rateioMicrorregiaoDtoList, rateioCidadeDtoList);
	}

	@Transactional
	public RateioDto atualizar(Projeto projeto, RateioDto rateioDto) {
		logger.info("Alterando dados do rateio para o Projeto com id: {}", projeto.getId());

		List<RateioMicrorregiaoDto> rateioMicrorregiaoDtoList = this.atualizarRateioMicrorregiao(projeto, rateioDto.rateioMicrorregiao());

		List<RateioCidadeDto> rateioCidadeDtoList = this.atualizarRateioCidade(projeto, rateioDto.rateioCidade());

		logger.info("Rateio para o Projeto alterado com sucesso");
		return new RateioDto(rateioMicrorregiaoDtoList, rateioCidadeDtoList);
	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {
		logger.info("Excluindo rateio para o Projeto com id: {}", projeto.getId());

		Set<ProjetoMicrorregiao> projetoMicrorregiaoSet = this.buscarProjetoMicrorregiaoSetPorProjeto(projeto);

		this.excluirRateioMicrorregiao(projetoMicrorregiaoSet);

		Set<ProjetoCidade> projetoCidadeSet = this.buscarProjetoCidadeSetPorProjeto(projeto);

		this.excluirRateioCidade(projetoCidadeSet);

		logger.info("Rateio do Projeto excluido com sucesso");
	}

	private List<RateioMicrorregiaoDto> salvarRateioMicrorregiao(Projeto projeto, List<RateioMicrorregiaoDto> rateioMicrorregiaoDtoList) {
		logger.info("Cadastrando rateio por microrregioes para o Projeto com id: {}", projeto.getId());

		Set<ProjetoMicrorregiao> projetoMicrorregiaoSet = new HashSet<>();

		rateioMicrorregiaoDtoList.forEach(
					(rateioMicrorregiaoDto -> {
						ProjetoMicrorregiao projetoMicrorregiao = new ProjetoMicrorregiao(projeto, rateioMicrorregiaoDto);
						projetoMicrorregiaoSet.add(projetoMicrorregiao);
					})
		);

		List<ProjetoMicrorregiao> projetoMicrorregiaoList = projetoMicrorregiaoRepository.saveAll(projetoMicrorregiaoSet);

		logger.info("Rateio por microrregioes para o Projeto cadastrado com sucesso");
		return this.montarListaRateioMicrorregiaoDto(projetoMicrorregiaoList);
	}

	private List<RateioCidadeDto> salvarRateioCidade(Projeto projeto, List<RateioCidadeDto> rateioCidadeDtoList) {
		logger.info("Cadastrando rateio por cidades para o Projeto com id: {}", projeto.getId());

		Set<ProjetoCidade> projetoCidadeSet = new HashSet<>();

		rateioCidadeDtoList.forEach(
					(rateioCidadeDto -> {
						ProjetoCidade projetoCidade = new ProjetoCidade(projeto, rateioCidadeDto);
						projetoCidadeSet.add(projetoCidade);
					})
		);

		List<ProjetoCidade> projetoCidadeList = projetoCidadeRepository.saveAll(projetoCidadeSet);

		logger.info("Rateio por cidades para o Projeto cadastrado com sucesso");
		return this.montarListaRateioCidadeDto(projetoCidadeList);
	}

	private List<RateioMicrorregiaoDto> atualizarRateioMicrorregiao(Projeto projeto, List<RateioMicrorregiaoDto> rateioMicrorregiaoDtoList) {
		logger.info("Alterando dados do rateio por microrregioes para o Projeto com id: {}", projeto.getId());

		Set<ProjetoMicrorregiao> projetoMicrorregiaoSet = this.buscarProjetoMicrorregiaoSetPorProjeto(projeto);

		Set<ProjetoMicrorregiao> projetoMicrorregiaoRemoverSet = new HashSet<>();

		Set<ProjetoMicrorregiao> projetoMicrorregiaoAlterarSet = new HashSet<>();

		Set<ProjetoMicrorregiao> projetoMicrorregiaoAdicionarSet = new HashSet<>();


		projetoMicrorregiaoSet.forEach(
					projetoMicrorregiao -> {
						if (rateioMicrorregiaoDtoList
									.stream()
									.noneMatch(projetoMicrorregiao::compararIdMicrorregiaoComRateioMicrorregiaoDto)) {
							projetoMicrorregiao.apagar();
							projetoMicrorregiaoRemoverSet.add(projetoMicrorregiao);
						}
					}
		);

		rateioMicrorregiaoDtoList.forEach(
					rateioMicrorregiaoDto -> {
						projetoMicrorregiaoSet
									.stream()
									.filter(projetoMicrorregiao -> projetoMicrorregiao.compararIdMicrorregiaoComRateioMicrorregiaoDto(rateioMicrorregiaoDto))
									.findFirst()
									.ifPresentOrElse(
												(projetoMicrorregiao -> {
													if (!this.compararProjetoMicrorregiaoComRateioMicrorregiaoDto(projetoMicrorregiao, rateioMicrorregiaoDto)) {
														projetoMicrorregiao.apagar();
														projetoMicrorregiaoAlterarSet.add(projetoMicrorregiao);
														projetoMicrorregiaoAdicionarSet.add(new ProjetoMicrorregiao(projeto, rateioMicrorregiaoDto));
													}
												}),
												() -> {
													projetoMicrorregiaoAdicionarSet.add(new ProjetoMicrorregiao(projeto, rateioMicrorregiaoDto));
												}
									);
					}
		);


		projetoMicrorregiaoRemoverSet.addAll(projetoMicrorregiaoAlterarSet);

		projetoMicrorregiaoRemoverSet.addAll(projetoMicrorregiaoAdicionarSet);

		List<ProjetoMicrorregiao> projetoMicrorregiaoList = projetoMicrorregiaoRepository.saveAllAndFlush(projetoMicrorregiaoRemoverSet);

		logger.info("Rateio por microrregioes para o Projeto alterado com sucesso");
		return this.buscarProjetoMicrorregiaoSetPorProjeto(projeto).stream()
					.map(RateioMicrorregiaoDto::new)
					.toList();
	}

	private List<RateioCidadeDto> atualizarRateioCidade(Projeto projeto, List<RateioCidadeDto> rateioCidadeDtoList) {
		logger.info("Alterando dados do rateio por cidades para o Projeto com id: {}", projeto.getId());

		Set<ProjetoCidade> projetoCidadeSet = this.buscarProjetoCidadeSetPorProjeto(projeto);

		Set<ProjetoCidade> projetoCidadeRemoverSet = new HashSet<>();

		Set<ProjetoCidade> projetoCidadeAlterarSet = new HashSet<>();

		Set<ProjetoCidade> projetoCidadeAdicionarSet = new HashSet<>();

		projetoCidadeSet.forEach(
					projetoCidade -> {
						if (rateioCidadeDtoList
									.stream()
									.noneMatch(projetoCidade::compararIdCidadeComRateioCidadeDto)) {
							projetoCidade.apagar();
							projetoCidadeRemoverSet.add(projetoCidade);
						}
					}
		);

		rateioCidadeDtoList.forEach(
					rateioCidadeDto -> {
						projetoCidadeSet
									.stream()
									.filter(projetoCidade -> projetoCidade.compararIdCidadeComRateioCidadeDto(rateioCidadeDto))
									.findFirst()
									.ifPresentOrElse(
												(projetoCidade) -> {
													if (!this.compararProjetoCidadeComRateioCidadeDto(projetoCidade, rateioCidadeDto)) {
														projetoCidade.apagar();
														projetoCidadeAlterarSet.add(projetoCidade);
														projetoCidadeAlterarSet.add(new ProjetoCidade(projeto, rateioCidadeDto));
													}
												},
												() -> {
													projetoCidadeAdicionarSet.add(new ProjetoCidade(projeto, rateioCidadeDto));
												}
									);
					}
		);

		projetoCidadeRemoverSet.addAll(projetoCidadeAlterarSet);

		projetoCidadeRemoverSet.addAll(projetoCidadeAdicionarSet);

		List<ProjetoCidade> projetoCidadeList = projetoCidadeRepository.saveAllAndFlush(projetoCidadeRemoverSet);

		logger.info("Rateio por cidades para o Projeto alterado com sucesso");
		return this.buscarProjetoCidadeSetPorProjeto(projeto).stream()
					.map(RateioCidadeDto::new)
					.toList();

	}

	private void excluirRateioMicrorregiao(Set<ProjetoMicrorregiao> projetoMicrorregiaoSet) {
		logger.info("Excluindo rateio por microrregioes para o Projeto");

		projetoMicrorregiaoSet.forEach(ProjetoMicrorregiao::apagar);

		List<ProjetoMicrorregiao> projetoMicrorregiaoList = projetoMicrorregiaoRepository.saveAllAndFlush(projetoMicrorregiaoSet);

		projetoMicrorregiaoRepository.deleteAll(projetoMicrorregiaoList);

		logger.info("Rateio por microrregioes para o Projeto excluido com sucesso");
	}

	private void excluirRateioCidade(Set<ProjetoCidade> projetoCidadeSet) {
		logger.info("Excluindo rateio por cidades para o Projeto");

		projetoCidadeSet.forEach(ProjetoCidade::apagar);

		List<ProjetoCidade> projetoCidadeList = projetoCidadeRepository.saveAllAndFlush(projetoCidadeSet);

		projetoCidadeRepository.deleteAll(projetoCidadeList);

		logger.info("Rateio por cidades para o Projeto excluido com sucesso");
	}

	private Set<ProjetoMicrorregiao> buscarProjetoMicrorregiaoSetPorProjeto(Projeto projeto) {
		return projetoMicrorregiaoRepository.findAllByProjeto(projeto);
	}

	private Set<ProjetoCidade> buscarProjetoCidadeSetPorProjeto(Projeto projeto) {
		return projetoCidadeRepository.findAllByProjeto(projeto);
	}

	private List<RateioMicrorregiaoDto> montarListaRateioMicrorregiaoDto(List<ProjetoMicrorregiao> projetoMicrorregiaoList) {
		return projetoMicrorregiaoList
					.stream()
					.map(RateioMicrorregiaoDto::new)
					.toList();
	}

	private List<RateioCidadeDto> montarListaRateioCidadeDto(List<ProjetoCidade> projetoCidadeList) {
		return projetoCidadeList
					.stream()
					.map(RateioCidadeDto::new)
					.toList();
	}

	private boolean compararProjetoMicrorregiaoComRateioMicrorregiaoDto(ProjetoMicrorregiao projetoMicrorregiao, RateioMicrorregiaoDto rateioMicrorregiaoDto) {
		BigDecimal quantiaProjetoMicrorregiao = projetoMicrorregiao.getQuantia().setScale(2, RoundingMode.FLOOR);
		BigDecimal quantiaRateioMicrorregiaoDto = rateioMicrorregiaoDto.quantia().setScale(2, RoundingMode.FLOOR);
		boolean equalsQuantia = quantiaProjetoMicrorregiao.equals(quantiaRateioMicrorregiaoDto);

		BigDecimal percentualProjetoMicrorregiao = projetoMicrorregiao.getPercentual().setScale(2, RoundingMode.FLOOR);
		BigDecimal percentualRateioMicrorregiaoDto = rateioMicrorregiaoDto.percentual().setScale(2, RoundingMode.FLOOR);
		boolean equalsPercentual = percentualProjetoMicrorregiao.equals(percentualRateioMicrorregiaoDto);

		return equalsPercentual && equalsQuantia;
	}

	private boolean compararProjetoCidadeComRateioCidadeDto(ProjetoCidade projetoCidade, RateioCidadeDto rateioCidadeDto) {
		BigDecimal quantiaProjetoCidade = projetoCidade.getQuantia().setScale(2, RoundingMode.FLOOR);
		BigDecimal quantiaRateioCidadeDto = rateioCidadeDto.quantia().setScale(2, RoundingMode.FLOOR);
		boolean equalsQuantia = quantiaProjetoCidade.equals(quantiaRateioCidadeDto);

		BigDecimal percentualProjetoCidade = projetoCidade.getPercentual().setScale(2, RoundingMode.FLOOR);
		BigDecimal percentualRateioCidadeDto = rateioCidadeDto.percentual().setScale(2, RoundingMode.FLOOR);
		boolean equalsPercentual = percentualProjetoCidade.equals(percentualRateioCidadeDto);

		return equalsPercentual && equalsQuantia;
	}
}
