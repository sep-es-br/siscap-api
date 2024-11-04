package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.RateioDto;
import br.gov.es.siscap.dto.ValorDto;
import br.gov.es.siscap.enums.MoedaEnum;
import br.gov.es.siscap.enums.TipoValorEnum;
import br.gov.es.siscap.models.Localidade;
import br.gov.es.siscap.models.LocalidadeQuantia;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.LocalidadeQuantiaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalidadeQuantiaService {

	private final LocalidadeQuantiaRepository repository;
	private final Logger logger = LogManager.getLogger(LocalidadeQuantiaService.class.getName());

	public BigDecimal somarValorEstimadoTodosProjetos() {
		return repository.somarValorEstimadoTodosProjetos();
	}

	public Set<LocalidadeQuantia> buscarPorProjeto(Projeto projeto) {
		return repository.findAllByProjeto(projeto);
	}

	public Set<LocalidadeQuantia> buscarPorPrograma(Programa programa) {
		return repository.buscarPorPrograma(programa);
	}

	public List<String> listarNomesLocalidadesRateio(Set<LocalidadeQuantia> localidadeQuantiaSet) {
		return localidadeQuantiaSet.stream()
					.map(LocalidadeQuantia::getLocalidade)
					.map(Localidade::getNome)
					.toList();
	}

	public ValorDto montarValorDto(Set<LocalidadeQuantia> localidadeQuantiaSet) {

		Optional<LocalidadeQuantia> localidadeQuantiaOptional = localidadeQuantiaSet.stream().findAny();

		Long tipoValorDto = localidadeQuantiaOptional.isPresent() ? localidadeQuantiaOptional.get().getTipoValor().getId() : TipoValorEnum.ESTIMADO.getValue();

		String moeda = localidadeQuantiaOptional.isPresent() ? localidadeQuantiaOptional.get().getMoeda() : MoedaEnum.BRL.toString();

		BigDecimal quantia = this.calcularValorTotal(localidadeQuantiaSet);

		return new ValorDto(quantia, tipoValorDto, moeda);
	}

	public List<RateioDto> montarListRateioDtoPorProjeto(Set<LocalidadeQuantia> localidadeQuantiaSet) {

		BigDecimal total = this.calcularValorTotal(localidadeQuantiaSet);

		return localidadeQuantiaSet.stream()
					.map(localidadeQuantia -> {
						BigDecimal percentual = localidadeQuantia.getQuantia()
									.divide(total, 2, RoundingMode.HALF_UP)
									.multiply(BigDecimal.valueOf(100));
						return new RateioDto(localidadeQuantia.getLocalidade().getId(), percentual, localidadeQuantia.getQuantia());
					}).toList();
	}

	@Transactional
	public Set<LocalidadeQuantia> cadastrar(Projeto projeto, ValorDto valorDto, List<RateioDto> rateioDtoList) {
		logger.info("Cadastrando dados do rateio para o Projeto com id: {}", projeto.getId());

		Set<LocalidadeQuantia> localidadeQuantiaSet = new HashSet<>();

		rateioDtoList.forEach(rateioDto -> {
			LocalidadeQuantia localidadeQuantia = new LocalidadeQuantia(projeto, valorDto, rateioDto);
			localidadeQuantiaSet.add(localidadeQuantia);
		});

		List<LocalidadeQuantia> localidadeQuantiaSetResult = repository.saveAllAndFlush(localidadeQuantiaSet);

		logger.info("Rateio para o Projeto cadastrado com sucesso");
		return new HashSet<>(localidadeQuantiaSetResult);
	}

	@Transactional
	public Set<LocalidadeQuantia> atualizar(Projeto projeto, ValorDto valorDto, List<RateioDto> rateioDtoList) {
		logger.info("Atualizando dados do rateio para o Projeto com id: {}", projeto.getId());

		Set<LocalidadeQuantia> localidadeQuantiaSet = this.buscarPorProjeto(projeto);

		Set<LocalidadeQuantia> localidadeQuantiaAdicionarSet = new HashSet<>();
		Set<LocalidadeQuantia> localidadeQuantiaRemoverSet = new HashSet<>();
		Set<LocalidadeQuantia> localidadeQuantiaAlterarSet = new HashSet<>();

		localidadeQuantiaSet.forEach(localidadeQuantia -> {
			if (rateioDtoList.stream().noneMatch(rateioDto -> rateioDto.idLocalidade().equals(localidadeQuantia.getLocalidade().getId()))) {
				localidadeQuantia.apagarLocalidadeQuantia();
				localidadeQuantiaRemoverSet.add(localidadeQuantia);
			}
		});

		rateioDtoList.forEach(rateioDto -> {
			Optional<LocalidadeQuantia> localidadeQuantiaOptional = this.filtrarPorLocalidade(localidadeQuantiaSet, rateioDto.idLocalidade());

			if (localidadeQuantiaOptional.isPresent()) {
				LocalidadeQuantia localidadeQuantia = localidadeQuantiaOptional.get();
				localidadeQuantia.atualizarLocalidadeQuantia(valorDto, rateioDto);
				localidadeQuantiaAlterarSet.add(localidadeQuantia);
			} else {
				LocalidadeQuantia localidadeQuantia = new LocalidadeQuantia(projeto, valorDto, rateioDto);
				localidadeQuantiaAdicionarSet.add(localidadeQuantia);
			}
		});

		localidadeQuantiaRemoverSet.addAll(localidadeQuantiaAlterarSet);
		localidadeQuantiaRemoverSet.addAll(localidadeQuantiaAdicionarSet);

		List<LocalidadeQuantia> localidadeQuantiaSetResult = repository.saveAllAndFlush(localidadeQuantiaRemoverSet);

		logger.info("Rateio para o Projeto atualizado com sucesso");
		return new HashSet<>(localidadeQuantiaSetResult);
	}

	@Transactional
	public void excluir(Projeto projeto) {
		logger.info("Excluindo dados do rateio para o Projeto com id: {}", projeto.getId());

		Set<LocalidadeQuantia> localidadeQuantiaSet = this.buscarPorProjeto(projeto);
		localidadeQuantiaSet.forEach(LocalidadeQuantia::apagarLocalidadeQuantia);
		repository.saveAllAndFlush(localidadeQuantiaSet);

		logger.info("Rateio para o Projeto excluído com sucesso");
	}


	private BigDecimal calcularValorTotal(Set<LocalidadeQuantia> localidadeQuantiaSet) {
		return localidadeQuantiaSet.stream()
					.map(LocalidadeQuantia::getQuantia)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private Optional<LocalidadeQuantia> filtrarPorLocalidade(Set<LocalidadeQuantia> localidadeQuantiaSet, Long idLocalidade) {
		return localidadeQuantiaSet.stream()
					.filter(localidadeQuantia -> localidadeQuantia.getLocalidade().getId().equals(idLocalidade))
					.findFirst();
	}
}