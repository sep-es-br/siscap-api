package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.CartaConsultaDestinatariosDto;
import br.gov.es.siscap.models.CartaConsulta;
import br.gov.es.siscap.models.CartaConsultaDestinatario;
import br.gov.es.siscap.repository.CartaConsultaDestinatariosRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.gov.es.siscap.models.Organizacao;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartaConsultaDestinatariosService {

	private final CartaConsultaDestinatariosRepository repository;
	private final OrganizacaoService organizacaoService;
	private final Logger logger = LogManager.getLogger(LocalidadeQuantiaService.class.getName());

	@Transactional
	public Set<CartaConsultaDestinatario> cadastrar(CartaConsulta cartaConsulta,
			List<CartaConsultaDestinatariosDto> cartaConsultaDestinatariosDtoList) {

		logger.info("Cadastrando Organizações Pesquisa Intituições Financeiro com id: {}",
				cartaConsulta.getId());

		Set<CartaConsultaDestinatario> cartaConsultaDestinatariosSet = new HashSet<>();

		cartaConsultaDestinatariosDtoList.forEach(destinatario -> {
			Organizacao organizacao = organizacaoService.buscar(destinatario.idOrganizacao());
			CartaConsultaDestinatario cartaConsultaDestinatario = new CartaConsultaDestinatario(cartaConsulta,
					organizacao);
			cartaConsultaDestinatariosSet.add(cartaConsultaDestinatario);
		});

		List<CartaConsultaDestinatario> cartaConsultaDestinatariosSetResult = repository
				.saveAllAndFlush(cartaConsultaDestinatariosSet);

		logger.info("Destinatários Pesquisa Instituição Financeira cadastrados com sucesso");

		return new HashSet<>(cartaConsultaDestinatariosSetResult);

	}

	@Transactional
	public Set<CartaConsultaDestinatario> atualizar(CartaConsulta cartaConsulta,
			List<CartaConsultaDestinatariosDto> destinatariosDtoList ) {

		Set<CartaConsultaDestinatario> destinatariosCartaAtualizarSet = new HashSet<>();

		logger.info("Atualizando dados dos destinatários da Pesquisa de Fontes de Financiamento id: {}",
				cartaConsulta.getId());

		Set<CartaConsultaDestinatario> destinatariosCartaAdicionarSet = new HashSet<>(); // nao possuem id de
																							// destinatario
		destinatariosDtoList.forEach(destinatarioDto -> {
			if (destinatarioDto.id() == null || destinatarioDto.id() == 0) {
				Organizacao organizacao = organizacaoService.buscar(destinatarioDto.idOrganizacao());
				destinatariosCartaAdicionarSet.add(new CartaConsultaDestinatario(cartaConsulta, organizacao));
			}
		});

		Set<CartaConsultaDestinatario> destinatariosCartaRemoverSet = new HashSet<>(); // nao estao no set atual mas
																						// possuem id de destinatario
		Set<CartaConsultaDestinatario> destinatariosCartaAlterarSet = new HashSet<>(); // estao no set atual, tem id
																						// destinatario e id da carta -
																						// nao mudou

		Set<CartaConsultaDestinatario> destinatariosCartaSet = this.buscarPorCartaConsulta(cartaConsulta);
		destinatariosCartaSet.forEach(destinatarioCarta -> {
			if (!destinatariosDtoList.stream().anyMatch(destinatarioDto -> destinatarioDto.id() != null
					&& destinatarioDto.id().equals(destinatarioCarta.getId()))) {
				destinatariosCartaRemoverSet.add(destinatarioCarta);
			} else {
				destinatariosCartaAlterarSet.add(destinatarioCarta);
			}
		});

		repository.deleteAll(destinatariosCartaRemoverSet);

		destinatariosCartaAtualizarSet.addAll(destinatariosCartaAlterarSet);
		destinatariosCartaAtualizarSet.addAll(destinatariosCartaAdicionarSet);

		List<CartaConsultaDestinatario> cartaConsultaDestinatariosSetResult = repository
				.saveAllAndFlush(destinatariosCartaAtualizarSet);

		logger.info("Destinatários para fonte de pesquisa atualizado com sucesso");

		return new HashSet<>(cartaConsultaDestinatariosSetResult);

	}

	private Set<CartaConsultaDestinatario> buscarPorCartaConsulta(CartaConsulta cartaConsulta) {
		return repository.findAllByCartaConsulta(cartaConsulta);
	}

}