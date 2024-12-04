package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.InteressadoDto;
import br.gov.es.siscap.models.Prospeccao;
import br.gov.es.siscap.models.ProspeccaoInteressado;
import br.gov.es.siscap.repository.ProspeccaoInteressadoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProspeccaoInteressadoService {

	private final ProspeccaoInteressadoRepository repository;

	private final Logger logger = LogManager.getLogger(ProspeccaoInteressadoService.class);

	public List<InteressadoDto> buscarPorProspeccao(Prospeccao prospeccao) {
		return this.mapearProspeccaoInteressadoListParaInteressadoDtoList(new ArrayList<>(this.buscar(prospeccao)));
	}

	public List<String> buscarNomesInteressadosPorProspeccao(Prospeccao prospeccao) {
		return this.buscar(prospeccao).stream()
					.map(prospeccaoInteressado -> prospeccaoInteressado.getPessoa().getNome())
					.toList();
	}

	@Transactional
	public List<InteressadoDto> cadastrar(Prospeccao prospeccao, List<InteressadoDto> interessadosList) {
		logger.info("Cadastrando interessados na prospeccao com id: {}", prospeccao.getId());

		Set<ProspeccaoInteressado> prospeccaoInteressadoSet = new HashSet<>();

		interessadosList.forEach(interessado -> {
			ProspeccaoInteressado prospeccaoInteressado = new ProspeccaoInteressado(prospeccao, interessado);
			prospeccaoInteressadoSet.add(prospeccaoInteressado);
		});

		List<ProspeccaoInteressado> prospeccaoInteressadoList = repository.saveAll(prospeccaoInteressadoSet);

		logger.info("Interessados na prospeccao cadastrados com sucesso");
		return this.mapearProspeccaoInteressadoListParaInteressadoDtoList(prospeccaoInteressadoList);
	}

	@Transactional
	public List<InteressadoDto> atualizar(Prospeccao prospeccao, List<InteressadoDto> interessadosList) {
		logger.info("Alterando dados dos interessados na prospeccao com id: {}", prospeccao.getId());

		Set<ProspeccaoInteressado> prospeccaoInteressadoSet = this.buscar(prospeccao);

		Set<ProspeccaoInteressado> prospeccaoInteressadoAdicionarSet = new HashSet<>();
		Set<ProspeccaoInteressado> prospeccaoInteressadoRemoverSet = new HashSet<>();
		Set<ProspeccaoInteressado> prospeccaoInteressadoAlterarSet = new HashSet<>();


		prospeccaoInteressadoSet.forEach(prospeccaoInteressado -> {
			if (interessadosList.stream().noneMatch(interessadoDto -> interessadoDto.idInteressado().equals(prospeccaoInteressado.getPessoa().getId()))) {
				prospeccaoInteressado.apagarProspeccaoInteressado();
				prospeccaoInteressadoRemoverSet.add(prospeccaoInteressado);
			}
		});

		interessadosList.forEach(interessadoDto -> {
			Optional<ProspeccaoInteressado> prospeccaoInteressadoOptional = prospeccaoInteressadoSet
						.stream()
						.filter(prospeccaoInteressado -> this.compararProspeccaoInteressadoComInteressadoDto(prospeccaoInteressado, interessadoDto))
						.findFirst();

			if (prospeccaoInteressadoOptional.isPresent()) {
				ProspeccaoInteressado prospeccaoInteressado = prospeccaoInteressadoOptional.get();
				prospeccaoInteressado.atualizarProspeccaoInteressado(interessadoDto);
				prospeccaoInteressadoAlterarSet.add(prospeccaoInteressado);
			} else {
				ProspeccaoInteressado prospeccaoInteressado = new ProspeccaoInteressado(prospeccao, interessadoDto);
				prospeccaoInteressadoAdicionarSet.add(prospeccaoInteressado);
			}
		});

		prospeccaoInteressadoRemoverSet.addAll(prospeccaoInteressadoAlterarSet);
		prospeccaoInteressadoRemoverSet.addAll(prospeccaoInteressadoAdicionarSet);

		List<ProspeccaoInteressado> prospeccaoInteressadoListResult = repository.saveAllAndFlush(prospeccaoInteressadoRemoverSet);

		logger.info("Interessados na prospeccao atualizados com sucesso");
		return this.mapearProspeccaoInteressadoListParaInteressadoDtoList(prospeccaoInteressadoListResult);
	}

	@Transactional
	public void excluir(Prospeccao prospeccao) {
		logger.info("Excluindo interessados na prospeccao com id: {}", prospeccao.getId());

		Set<ProspeccaoInteressado> prospeccaoInteressadoSet = this.buscar(prospeccao);
		prospeccaoInteressadoSet.forEach(ProspeccaoInteressado::apagarProspeccaoInteressado);
		repository.saveAllAndFlush(prospeccaoInteressadoSet);

		logger.info("Interessados na prospeccao excluidos com sucesso");
	}

	private Set<ProspeccaoInteressado> buscar(Prospeccao prospeccao) {
		return repository.findAllByProspeccao(prospeccao);
	}

	private List<InteressadoDto> mapearProspeccaoInteressadoListParaInteressadoDtoList(List<ProspeccaoInteressado> prospeccaoInteressadoList) {
		return prospeccaoInteressadoList.stream().map(InteressadoDto::new).toList();
	}

	private boolean compararProspeccaoInteressadoComInteressadoDto(ProspeccaoInteressado prospeccaoInteressado, InteressadoDto interessadoDto) {
		return prospeccaoInteressado.getPessoa().getId().equals(interessadoDto.idInteressado());
	}
}