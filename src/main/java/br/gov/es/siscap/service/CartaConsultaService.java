package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.*;
import br.gov.es.siscap.dto.listagem.CartaConsultaListaDto;
import br.gov.es.siscap.form.CartaConsultaForm;
import br.gov.es.siscap.models.CartaConsulta;
import br.gov.es.siscap.repository.CartaConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartaConsultaService {

	private final CartaConsultaRepository repository;
	private final DocumentosService documentosService;
	private final ProgramaProjetoService programaProjetoService;
	private final ProgramaValorService programaValorService;
	private final ProjetoValorService projetoValorService;

	public Page<CartaConsultaListaDto> listarTodos(Pageable pageable) {
		return repository.findAll(pageable).map(CartaConsultaListaDto::new);
	}

	public CartaConsultaDto buscarPorId(Long id) {

		CartaConsulta cartaConsulta = repository.findById(id).orElseThrow();

		String corpo = documentosService.buscarCartaConsultaCorpo(cartaConsulta.getNomeDocumento());

		return new CartaConsultaDto(cartaConsulta, corpo);
	}

	public CartaConsultaDetalhesDto buscarDetalhesPorId(Long id) {

		CartaConsulta cartaConsulta = repository.findById(id).orElseThrow();

		String corpo = documentosService.buscarCartaConsultaCorpo(cartaConsulta.getNomeDocumento());

		List<SelectDto> projetosPropostos = this.construirProjetosPropostosList(cartaConsulta);

		ValorDto valor = this.construirValorDto(cartaConsulta);

		return new CartaConsultaDetalhesDto(cartaConsulta.getId(), cartaConsulta.getCartaConsultaObjeto(), projetosPropostos, valor, corpo);
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
		CartaConsulta cartaConsulta = repository.findById(id).orElseThrow();

		cartaConsulta.atualizarCartaConsulta(form);

		documentosService.atualizarCartaConsultaCorpo(cartaConsulta.getNomeDocumento(), form.corpo());

		CartaConsulta cartaConsultaResultado = repository.save(cartaConsulta);

		return new CartaConsultaDto(cartaConsultaResultado, form.corpo());
	}

	@Transactional
	public void excluir(Long id) {
		CartaConsulta cartaConsulta = repository.findById(id).orElseThrow();

		cartaConsulta.apagarCartaConsulta();

		documentosService.excluirCartaConsultaCorpo(cartaConsulta.getNomeDocumento());

		repository.save(cartaConsulta);
	}

	private List<SelectDto> construirProjetosPropostosList(CartaConsulta cartaConsulta) {
		if (cartaConsulta.getProjeto() != null && cartaConsulta.getPrograma() == null) {
			return List.of();
		}

		return programaProjetoService.buscarSelectDtoPorIdPrograma(cartaConsulta.getPrograma().getId());
	}

	private ValorDto construirValorDto(CartaConsulta cartaConsulta) {
		if (cartaConsulta.getPrograma() != null && cartaConsulta.getProjeto() == null) {
			return programaValorService.buscarPorPrograma(cartaConsulta.getPrograma());
		}

		if (cartaConsulta.getProjeto() != null && cartaConsulta.getPrograma() == null) {
			return projetoValorService.buscarPorProjeto(cartaConsulta.getProjeto());
		}

		return null;
	}
}
