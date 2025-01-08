package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.CartaConsultaDetalhesDto;
import br.gov.es.siscap.dto.InteressadoDto;
import br.gov.es.siscap.dto.ProspeccaoDetalhesDto;
import br.gov.es.siscap.dto.ProspeccaoDto;
import br.gov.es.siscap.dto.listagem.ProspeccaoListaDto;
import br.gov.es.siscap.form.ProspeccaoForm;
import br.gov.es.siscap.models.Prospeccao;
import br.gov.es.siscap.repository.ProspeccaoRepository;
import br.gov.es.siscap.utils.FormatadorCountAno;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProspeccaoService {

	private final ProspeccaoRepository repository;
	private final ProspeccaoInteressadoService prospeccaoInteressadoService;
	private final CartaConsultaService cartaConsultaService;

	private final Logger logger = LogManager.getLogger(ProspeccaoService.class);

	public Page<ProspeccaoListaDto> listarTodos(Pageable pageable, String search) {
		logger.info("Buscando todas as prospeccoes");

		return repository.paginarProspeccoesPorFiltroPesquisaSimples(search, pageable)
					.map(ProspeccaoListaDto::new);
	}

	public ProspeccaoDto buscarPorId(Long id) {
		logger.info("Buscando prospeccao com id: {}", id);

		Prospeccao prospeccao = this.buscar(id);

		List<InteressadoDto> interessadoDtoList = prospeccaoInteressadoService.buscarPorProspeccao(prospeccao);

		return new ProspeccaoDto(prospeccao, interessadoDtoList);
	}

	public ProspeccaoDetalhesDto buscarDetalhesPorId(Long id) {

		Prospeccao prospeccao = this.buscar(id);

		List<String> nomesInteressados = prospeccaoInteressadoService.buscarNomesInteressadosPorProspeccao(prospeccao);

		CartaConsultaDetalhesDto cartaConsultaDetalhesDto = cartaConsultaService.buscarDetalhesPorId(prospeccao.getCartaConsulta().getId());

		return new ProspeccaoDetalhesDto(prospeccao, nomesInteressados, cartaConsultaDetalhesDto);
	}

	@Transactional
	public ProspeccaoDto cadastrar(ProspeccaoForm form) {
		logger.info("Cadastrando nova prospeccao");
		logger.info("Dados: {}", form);

		Prospeccao tempProspeccao = new Prospeccao(form);

		tempProspeccao.setCountAno(buscarCountAnoFormatado());

		Prospeccao prospeccao = repository.save(tempProspeccao);

		List<InteressadoDto> interessadoDtoList = prospeccaoInteressadoService.cadastrar(prospeccao, form.interessadosList());

		logger.info("Prospeccao cadastrada com sucesso");
		return new ProspeccaoDto(prospeccao, interessadoDtoList);
	}

	@Transactional
	public ProspeccaoDto atualizar(Long id, ProspeccaoForm form) {
		logger.info("Atualizando prospeccao com id: {}", id);
		logger.info("Dados: {}", form);

		Prospeccao prospeccao = this.buscar(id);

		prospeccao.atualizar(form);

		Prospeccao prospeccaoResult = repository.save(prospeccao);

		List<InteressadoDto> interessadoDtoList = prospeccaoInteressadoService.atualizar(prospeccao, form.interessadosList());

		logger.info("Prospeccao atualizada com sucesso");
		return new ProspeccaoDto(prospeccaoResult, interessadoDtoList);
	}

	@Transactional
	public void excluir(Long id) {
		logger.info("Excluindo prospeccao com id: {}", id);

		Prospeccao prospeccao = this.buscar(id);

		prospeccao.apagarHistorico();

		repository.saveAndFlush(prospeccao);

		prospeccaoInteressadoService.excluir(prospeccao);

		logger.info("Prospeccao excluída com sucesso");
	}

	private Prospeccao buscar(Long id) {
		return repository.findById(id)
					.orElseThrow(() -> new RuntimeException("Prospeccao não encontrada"));
	}

	private String buscarCountAnoFormatado() {
		return FormatadorCountAno.formatar(repository.contagemAnoAtual());
	}
}