package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ValorDto;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoValor;
import br.gov.es.siscap.repository.ProjetoValorRepository;
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
public class ProjetoValorService {

	private final ProjetoValorRepository repository;
	private final Logger logger = LogManager.getLogger(ProjetoValorService.class);

	public ValorDto buscarPorProjeto(Projeto projeto) {
		ProjetoValor projetoValor = this.buscarProjetoValor(projeto);

		return new ValorDto(projetoValor);
	}

	@Transactional
	public ValorDto cadastrar(Projeto projeto, ValorDto valorDto) {
		logger.info("Cadastrando valor para o Projeto com id: {}", projeto.getId());

		ProjetoValor projetoValor = new ProjetoValor(projeto, valorDto);

		ProjetoValor projetoValorResult = repository.save(projetoValor);

		logger.info("Valor para o Projeto cadastrado com sucesso");
		return new ValorDto(projetoValorResult);
	}

	@Transactional
	public ValorDto atualizar(Projeto projeto, ValorDto valorDto) {
		logger.info("Atualizando valor para o Projeto com id: {}", projeto.getId());

		ProjetoValor projetoValor = this.buscarProjetoValor(projeto);

		if (this.compararProjetoValorComValorDto(projetoValor, valorDto)) {
			return new ValorDto(projetoValor);
		}

		projetoValor.apagarProjetoValor();
		repository.save(projetoValor);

		ProjetoValor projetoValorResult = repository.save(new ProjetoValor(projeto, valorDto));

		logger.info("Valor para o Projeto atualizado com sucesso");
		return new ValorDto(projetoValorResult);
	}

	@Transactional
	public void excluir(Projeto projeto) {
		logger.info("Excluindo valor para o Projeto com id: {}", projeto.getId());

		ProjetoValor projetoValor = this.buscarProjetoValor(projeto);

		projetoValor.apagarProjetoValor();
		repository.saveAndFlush(projetoValor);

		logger.info("Valor para o Projeto excluído com sucesso");
	}

	private ProjetoValor buscarProjetoValor(Projeto projeto) {
		return repository.findByProjeto(projeto);
	}

	private boolean compararProjetoValorComValorDto(ProjetoValor projetoValor, ValorDto valorDto) {
		boolean checkTipo = projetoValor.getValor().getId().equals(valorDto.tipo());
		boolean checkMoeda = projetoValor.getMoeda().equals(valorDto.moeda());

		BigDecimal quantiaProjetoValor = projetoValor.getQuantia().setScale(2, RoundingMode.FLOOR);
		BigDecimal quantiaValorDto = valorDto.quantia().setScale(2, RoundingMode.FLOOR);
		boolean checkQuantia = quantiaProjetoValor.equals(quantiaValorDto);

		return checkTipo && checkMoeda && checkQuantia;
	}
}
