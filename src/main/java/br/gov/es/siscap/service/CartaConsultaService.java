package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.*;
import br.gov.es.siscap.dto.listagem.CartaConsultaListaDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.exception.CartaConsultaObjetoInvalidoException;
import br.gov.es.siscap.form.CartaConsultaForm;
import br.gov.es.siscap.models.CartaConsulta;
import br.gov.es.siscap.models.LocalidadeQuantia;
import br.gov.es.siscap.repository.CartaConsultaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartaConsultaService {

	private final CartaConsultaRepository repository;
	private final DocumentosService documentosService;
	private final ProjetoService projetoService;
	private final LocalidadeQuantiaService localidadeQuantiaService;
	private final Logger logger = LogManager.getLogger(CartaConsultaService.class);

	public Page<CartaConsultaListaDto> listarTodos(Pageable pageable) {

		Page<CartaConsultaListaDto> cartasConsultaPaginadas = null;

		try {
			cartasConsultaPaginadas = repository.findAll(pageable).map(CartaConsultaListaDto::new);
		} catch (Exception e) {
			if (e.getClass().equals(EntityNotFoundException.class)) {
				logger.error("Erro ao listar as cartas consulta: {}", e.getMessage());
				throw new CartaConsultaObjetoInvalidoException("Não foi possível localizar o objeto de alguma carta consulta.");
			}
		}

		return cartasConsultaPaginadas;
	}

	public List<OpcoesDto> listarOpcoesDropdown() {
		return repository.findAll().stream().map(OpcoesDto::new).toList();
	}

	public CartaConsultaDto buscarPorId(Long id) {

		CartaConsulta cartaConsulta = this.buscarCartaConsulta(id);

		String corpo = documentosService.buscarCartaConsultaCorpo(cartaConsulta.getNomeDocumento());

		return new CartaConsultaDto(cartaConsulta, corpo);
	}

	public CartaConsultaDetalhesDto buscarDetalhesPorId(Long id) {

		CartaConsulta cartaConsulta = this.buscarCartaConsulta(id);

		List<OpcoesDto> projetosPropostos = this.construirProjetosPropostosList(cartaConsulta);

		ValorDto valor = this.construirValorDto(cartaConsulta);

		String corpo = documentosService.buscarCartaConsultaCorpo(cartaConsulta.getNomeDocumento());

		return new CartaConsultaDetalhesDto(cartaConsulta, projetosPropostos, valor, corpo);
	}

	@Transactional
	public CartaConsultaDto cadastrar(CartaConsultaForm form) {
		CartaConsulta cartaConsulta = new CartaConsulta(form);

		String nomeDocumento = documentosService.cadastrarCartaConsultaCorpo(form.corpo());

		cartaConsulta.setNomeDocumento(nomeDocumento);

		repository.save(cartaConsulta);

		return new CartaConsultaDto(cartaConsulta, form.corpo());
	}

	@Transactional
	public CartaConsultaDto atualizar(Long id, CartaConsultaForm form) {
		CartaConsulta cartaConsulta = this.buscarCartaConsulta(id);

		cartaConsulta.atualizarCartaConsulta(form);

		documentosService.atualizarCartaConsultaCorpo(cartaConsulta.getNomeDocumento(), form.corpo());

		CartaConsulta cartaConsultaResultado = repository.save(cartaConsulta);

		return new CartaConsultaDto(cartaConsultaResultado, form.corpo());
	}

	@Transactional
	public void excluir(Long id) {
		CartaConsulta cartaConsulta = this.buscarCartaConsulta(id);

		cartaConsulta.apagarCartaConsulta();

		documentosService.excluirCartaConsultaCorpo(cartaConsulta.getNomeDocumento());

		repository.save(cartaConsulta);
	}

	@Transactional
	public void alterarCartaConsultaProspectado(CartaConsulta cartaConsulta) {
		cartaConsulta.setProspectado(true);

		repository.save(cartaConsulta);
	}

	private CartaConsulta buscarCartaConsulta(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Carta de consulta não encontrada"));
	}

	private List<OpcoesDto> construirProjetosPropostosList(CartaConsulta cartaConsulta) {
		if (cartaConsulta.getProjeto() != null && cartaConsulta.getPrograma() == null) {
			return List.of();
		}

		return projetoService.buscarProjetosPropostos(cartaConsulta.getPrograma());
	}

	private ValorDto construirValorDto(CartaConsulta cartaConsulta) {
		if (cartaConsulta.getPrograma() != null && cartaConsulta.getProjeto() == null) {
			Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.buscarPorPrograma(cartaConsulta.getPrograma());
			return localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);
		}

		if (cartaConsulta.getProjeto() != null && cartaConsulta.getPrograma() == null) {
			Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.buscarPorProjeto(cartaConsulta.getProjeto());
			return localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);
		}

		return null;
	}
}