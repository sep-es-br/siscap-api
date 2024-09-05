package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ValorDto;
import br.gov.es.siscap.exception.ProgramaSemValorException;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaValor;
import br.gov.es.siscap.repository.ProgramaValorRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramaValorService {

	private final ProgramaValorRepository repository;
	private final Logger logger = LogManager.getLogger(ProgramaValorService.class);

	public ValorDto buscarPorPrograma(Programa programa) {
		List<ProgramaValor> programaValorList = this.buscarProgramaValorSetPorPrograma(programa).stream().toList();

		return this.montarValorDto(programaValorList);
	}

	@Transactional
	public ValorDto salvar(Programa programa, ValorDto valorDto) {
		logger.info("Cadastrando valor para o Programa com id: {}", programa.getId());

		Set<ProgramaValor> programaValorSet = new HashSet<>();

		programaValorSet.add(new ProgramaValor(programa, valorDto));

		List<ProgramaValor> programaValorList = repository.saveAll(programaValorSet);

		logger.info("Valor para o Programa cadastrado com sucesso");
		return this.montarValorDto(programaValorList);
	}

	@Transactional
	public ValorDto atualizar(Programa programa, ValorDto valorDto) {
		logger.info("Atualizando valor para o Programa com id: {}", programa.getId());

		ProgramaValor programaValor = this.buscarProgramaValorSetPorPrograma(programa)
					.stream()
					.findFirst()
					.orElseThrow(ProgramaSemValorException::new);

		if (programaValor.compararProgramaValorComValorDto(valorDto)) {
			return this.buscarPorPrograma(programa);
		}

		Set<ProgramaValor> programaValorAtualizarSet = new HashSet<>();

		programaValor.apagarProgramaValor();
		programaValorAtualizarSet.add(programaValor);
		programaValorAtualizarSet.add(new ProgramaValor(programa, valorDto));

		List<ProgramaValor> programaValorList = repository.saveAllAndFlush(programaValorAtualizarSet);

		logger.info("Valor para o Programa atualizado com sucesso");
		return this.buscarPorPrograma(programa);
	}

	@Transactional
	public void excluir(Programa programa) {
		logger.info("Excluindo valor para o Programa com id: {}", programa.getId());

		Set<ProgramaValor> programaValorSet = this.buscarProgramaValorSetPorPrograma(programa);

		programaValorSet.forEach(ProgramaValor::apagarProgramaValor);

		List<ProgramaValor> programaValorList = repository.saveAllAndFlush(programaValorSet);

		repository.deleteAll(programaValorList);

		logger.info("Valor para o Programa excluído com sucesso");
	}

	private Set<ProgramaValor> buscarProgramaValorSetPorPrograma(Programa programa) {
		return repository.findAllByPrograma(programa);
	}

	private ValorDto montarValorDto(List<ProgramaValor> programaValorList) {
		return programaValorList.stream()
					.findFirst()
					.map(ValorDto::new)
					.orElseThrow(ProgramaSemValorException::new);
	}
}
