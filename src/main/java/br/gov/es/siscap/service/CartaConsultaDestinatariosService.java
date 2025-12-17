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
	public Set<CartaConsultaDestinatario> cadastrar( CartaConsulta cartaConsulta,
			List<CartaConsultaDestinatariosDto> cartaConsultaDestinatariosDtoList) {

		logger.info("Cadastrando Organizações Pesquisa Intituições Financeiro com id: {}",
				cartaConsulta.getId());

		Set<CartaConsultaDestinatario> cartaConsultaDestinatariosSet = new HashSet<>();

		cartaConsultaDestinatariosDtoList.forEach( destinatario -> {
			Organizacao organizacao = organizacaoService.buscar(destinatario.idOrganizacao());
			CartaConsultaDestinatario cartaConsultaDestinatario = new CartaConsultaDestinatario( cartaConsulta, organizacao );
			cartaConsultaDestinatariosSet.add(cartaConsultaDestinatario);
		});

		List<CartaConsultaDestinatario> cartaConsultaDestinatariosSetResult = repository
				.saveAllAndFlush(cartaConsultaDestinatariosSet);

		logger.info("Destinatários Pesquisa Instituição Financeira cadastrados com sucesso");

		return new HashSet<>(cartaConsultaDestinatariosSetResult);

	}

	// private Set<CartaConsultaDestinatario> atualizarDestinatariosCartaConsulta(CartaConsulta cartaConsulta,
	// 		Set<CartaConsultaDestinatario> destinatariosExistentes, List<CartaConsultaDestinatariosDto> dtoList) {

	// 	Map<Long, CartaConsultaDestinatario> destinatariosExistentesMap = destinatariosExistentes.stream()
	// 			.filter(dest -> dest.getOrganizacao().getId() != null)
	// 			.collect(Collectors.toMap(CartaConsultaDestinatario::getOrganizacaoId, Function.identity()));

	// 	return dtoList.stream()
	// 			.map(dto -> {
	// 				CartaConsultaDestinatario destinatario;
	// 				if (dto.id() != null && destinatariosExistentesMap.containsKey(dto.id())) {
	// 					destinatario = destinatariosExistentesMap.get(dto.id());
	// 					destinatario.setId(dto.id());
	// 					destinatario.setCartaConsulta(cartaConsulta);
	// 					destinatario.setOrganizacao(organizacaoService.buscar(dto.idOrganizacao()));
	// 				} else {
	// 					Organizacao organizacaoDestinatario = organizacaoService.buscar(dto.idOrganizacao());
	// 					destinatario = new CartaConsultaDestinatario(cartaConsulta, organizacaoDestinatario);
	// 				}
	// 				return destinatario;
	// 			})
	// 			.collect(Collectors.toSet());

	// }

	@Transactional
	public Set<CartaConsultaDestinatario> atualizar(CartaConsulta cartaConsulta,
			List<CartaConsultaDestinatariosDto> destinatariosDtoList) {

		logger.info("Atualizando dados dos destinatários da Pesquisa de Fontes de Financiamento id: {}",
				cartaConsulta.getId());

		Set<CartaConsultaDestinatario> destinatariosCartaSet = this.buscarPorCartaConsulta(cartaConsulta);

		Set<CartaConsultaDestinatario> destinatariosCartaAdicionarSet = new HashSet<>();
		Set<CartaConsultaDestinatario> destinatariosCartaRemoverSet = new HashSet<>();
		Set<CartaConsultaDestinatario> destinatariosCartaAlterarSet = new HashSet<>();

		destinatariosCartaSet.forEach(destinatariosCarta -> {
			if (destinatariosDtoList.stream()
					.noneMatch(rateioDto -> rateioDto.idOrganizacao().equals(destinatariosCarta.getOrganizacaoId()))) {
				//destinatariosCarta.apagarLocalidadeQuantia();
				destinatariosCartaRemoverSet.add(destinatariosCarta);
			}
		});

		destinatariosCartaRemoverSet.addAll(destinatariosCartaAlterarSet);
		destinatariosCartaRemoverSet.addAll(destinatariosCartaAdicionarSet);

		List<CartaConsultaDestinatario> cartaConsultaDestinatariosSetResult = repository
				.saveAllAndFlush(destinatariosCartaRemoverSet);

		logger.info("Destinatários para fonte de pesquisa atualizado com sucesso");

		return new HashSet<>(cartaConsultaDestinatariosSetResult);

	}

	// @Transactional
	// public void excluir(Projeto projeto) {
	// logger.info("Excluindo dados do rateio para o Projeto com id: {}",
	// projeto.getId());

	// Set<LocalidadeQuantia> localidadeQuantiaSet = this.buscarPorProjeto(projeto);
	// localidadeQuantiaSet.forEach(LocalidadeQuantia::apagarLocalidadeQuantia);
	// repository.saveAllAndFlush(localidadeQuantiaSet);

	// logger.info("Rateio para o Projeto excluído com sucesso");
	// }

	// @Transactional
	// public void excluirFisicamentePorProjeto(Projeto projeto) {
	// logger.info("Excluindo fisicamente dados do rateio para o Projeto com id:
	// {}", projeto.getId());

	// repository.deleteFisicoPorProjeto(projeto.getId());

	// logger.info("Rateio para o Projeto excluído fisicamente com sucesso");
	// }

	// private BigDecimal calcularValorTotal(Set<LocalidadeQuantia>
	// localidadeQuantiaSet) {
	// return localidadeQuantiaSet.stream()
	// .map(LocalidadeQuantia::getQuantia)
	// .reduce(BigDecimal.ZERO, BigDecimal::add);
	// }

	private Set<CartaConsultaDestinatario> buscarPorCartaConsulta(CartaConsulta cartaConsulta) {
		return repository.findAllByCartaConsulta(cartaConsulta);
	}

	// private Optional<LocalidadeQuantia> filtrarPorLocalidade(Set<LocalidadeQuantia> localidadeQuantiaSet,
	// 		Long idLocalidade) {
	// 	return localidadeQuantiaSet.stream()
	// 			.filter(localidadeQuantia -> localidadeQuantia.getLocalidade().getId().equals(idLocalidade))
	// 			.findFirst();
	// }

}