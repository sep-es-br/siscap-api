package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.RateioDto;
import br.gov.es.siscap.models.Cidade;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoCidade;
import br.gov.es.siscap.repository.ProjetoCidadeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoCidadeService {

	private final ProjetoCidadeRepository projetoCidadeRepository;
	private final Logger logger = LogManager.getLogger(ProjetoCidadeService.class);

	public Set<ProjetoCidade> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando rateio do Projeto com id: {}", projeto.getId());
		return projetoCidadeRepository.findAllByProjeto(projeto);
	}

	public List<String> listarNomesCidadesPorProjeto(Projeto projeto) {
		Set<ProjetoCidade> projetoCidadeSet = projetoCidadeRepository.findAllByProjeto(projeto);

		return projetoCidadeSet
					.stream()
					.map(ProjetoCidade::getCidade)
					.map(Cidade::getNome)
					.toList();
	}

	@Transactional
	public Set<ProjetoCidade> salvar(Projeto projeto, List<RateioDto> rateioDtoList) {
		logger.info("Cadastrando rateio do Projeto com id: {}", projeto.getId());
		Set<ProjetoCidade> projetoCidadeSet = new HashSet<>();

		for (RateioDto rateioDto : rateioDtoList) {
			ProjetoCidade projetoCidade = new ProjetoCidade(projeto, rateioDto);
			projetoCidadeSet.add(projetoCidade);
		}

		List<ProjetoCidade> projetoCidadeList = projetoCidadeRepository.saveAll(projetoCidadeSet);

		logger.info("Rateio do projeto cadastrado com sucesso");
		return new HashSet<>(projetoCidadeList);
	}

	@Transactional
	public Set<ProjetoCidade> atualizar(Projeto projeto, List<RateioDto> rateioDtoList) {
		logger.info("Alterando rateio do Projeto com id: {}", projeto.getId());

		Set<ProjetoCidade> projetoCidadeSet = this.buscarPorProjeto(projeto);

		Set<ProjetoCidade> projetoCidadeRemoverSet = new HashSet<>();

		Set<ProjetoCidade> projetoCidadeAtualizarSet = new HashSet<>();

		Set<ProjetoCidade> projetoCidadesIncluirSet = new HashSet<>();

		projetoCidadeSet.forEach(projetoCidade -> {
			if (rateioDtoList.stream().noneMatch(projetoCidade::compararComRateioDto)) {
				projetoCidade.apagar();
				projetoCidadeRemoverSet.add(projetoCidade);
			}
		});

		rateioDtoList.forEach(rateioDto -> {
			projetoCidadeSet
						.stream()
						.filter(projetoCidade -> projetoCidade.compararComRateioDto(rateioDto))
						.findFirst()
						.ifPresentOrElse(
									(projetoCidade -> projetoCidadeAtualizarSet.addAll(this.atualizarRateio(projetoCidade, rateioDto))),
									() -> {
										projetoCidadesIncluirSet.add(new ProjetoCidade(projeto, rateioDto));
									}
						);
		});

		projetoCidadeRemoverSet.addAll(projetoCidadeAtualizarSet);

		projetoCidadeRemoverSet.addAll(projetoCidadesIncluirSet);

		projetoCidadeRepository.saveAll(projetoCidadeRemoverSet);

		logger.info("Rateio do projeto alterado com sucesso");
		return this.buscarPorProjeto(projeto);
	}

	@Transactional
	public void excluir(Projeto projeto) {
		logger.info("Excluindo rateio do Projeto com id: {}", projeto.getId());

		Set<ProjetoCidade> projetoCidadeSet = this.buscarPorProjeto(projeto);

		projetoCidadeSet.forEach(ProjetoCidade::apagar);

		List<ProjetoCidade> projetoCidadeList = projetoCidadeRepository.saveAllAndFlush(projetoCidadeSet);

		projetoCidadeRepository.deleteAll(projetoCidadeList);

		logger.info("Rateio do projeto excluido com sucesso");
	}

	private Set<ProjetoCidade> atualizarRateio(ProjetoCidade projetoCidade, RateioDto rateioDto) {
		Set<ProjetoCidade> projetoCidadeAtualizarSet = new HashSet<>();

		if (!Objects.equals(projetoCidade.getQuantia(), rateioDto.quantia())) {
			projetoCidade.apagar();
			projetoCidadeAtualizarSet.add(projetoCidade);

			projetoCidadeAtualizarSet.add(new ProjetoCidade(projetoCidade.getProjeto(), rateioDto));
		}

		return projetoCidadeAtualizarSet;
	}
}
