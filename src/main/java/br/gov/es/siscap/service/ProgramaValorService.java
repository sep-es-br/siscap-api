package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ValorDto;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaValor;
import br.gov.es.siscap.repository.ProgramaValorRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramaValorService {

	private final ProgramaValorRepository repository;
	private final Logger logger = LogManager.getLogger(ProgramaValorService.class);

	public ValorDto buscarPorPrograma(Programa programa) {
		ProgramaValor programaValor = this.buscarProgramaValor(programa);

		return new ValorDto(programaValor);
	}

	@Transactional
	public ValorDto cadastrar(Programa programa, ValorDto valorDto) {
		logger.info("Cadastrando valor para o Programa com id: {}", programa.getId());

		ProgramaValor programaValor = new ProgramaValor(programa, valorDto);

		ProgramaValor programaValorResult = repository.save(programaValor);

		logger.info("Valor para o Programa cadastrado com sucesso");
		return new ValorDto(programaValorResult);
	}

	@Transactional
	public ValorDto atualizar(Programa programa, ValorDto valorDto) {
		logger.info("Atualizando valor para o Programa com id: {}", programa.getId());

		ProgramaValor programaValor = this.buscarProgramaValor(programa);

		if (this.compararProgramaValorComValorDto(programaValor, valorDto)) {
			return new ValorDto(programaValor);
		}

		programaValor.apagarProgramaValor();
		repository.save(programaValor);

		ProgramaValor programaValorResult = repository.save(new ProgramaValor(programa, valorDto));

		logger.info("Valor para o Programa atualizado com sucesso");
		return new ValorDto(programaValorResult);
	}

	@Transactional
	public void excluir(Programa programa) {
		logger.info("Excluindo valor para o Programa com id: {}", programa.getId());

		ProgramaValor programaValor = this.buscarProgramaValor(programa);

		programaValor.apagarProgramaValor();
		repository.saveAndFlush(programaValor);

		logger.info("Valor para o Programa excluído com sucesso");
	}

	private ProgramaValor buscarProgramaValor(Programa programa) {
		return repository.findByPrograma(programa);
	}

	private boolean compararProgramaValorComValorDto(ProgramaValor programaValor, ValorDto valorDto) {
		boolean checkTipo = programaValor.getValor().getId().equals(valorDto.tipo());
		boolean checkMoeda = programaValor.getMoeda().equals(valorDto.moeda());

		BigDecimal quantiaProgramaValor = programaValor.getQuantia().setScale(2, RoundingMode.FLOOR);
		BigDecimal quantiaValorDto = valorDto.quantia().setScale(2, RoundingMode.FLOOR);
		boolean checkQuantia = quantiaProgramaValor.equals(quantiaValorDto);

		return checkTipo && checkMoeda && checkQuantia;
	}
}
