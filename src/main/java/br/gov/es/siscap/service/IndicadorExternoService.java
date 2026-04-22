package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.OrganizacaoDto;
import br.gov.es.siscap.dto.indicadoresexternos.LabelDTO;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesGestaoIndicadorDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.dto.listagem.OrganizacaoListaDto;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.OrganizacaoNaoEncontradaException;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.form.OrganizacaoForm;
import br.gov.es.siscap.models.IndicadorGestaoExterno;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.TipoOrganizacao;
import br.gov.es.siscap.repository.IndicadorGestaoExternoRepository;
import br.gov.es.siscap.repository.OrganizacaoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndicadorExternoService {

	private final IndicadorGestaoExternoRepository repository;

	private final Logger logger = LogManager.getLogger(IndicadorExternoService.class);

	public List<OpcoesGestaoIndicadorDto> listarGestoesAtivasIndicadores() {

		List<IndicadorGestaoExterno> gestoes = repository.findAllAtivasComLabels();

		return gestoes.stream()
				.map(gestao -> {

					List<LabelDTO> labels = Arrays.stream(gestao.getModelLabel().split(","))
							.map(String::trim)
							.filter(s -> !s.isEmpty())
							.map( nomeLabel -> new LabelDTO(
									null, // sem id
									nomeLabel, // nome vindo da string
									List.of() // sem valores (não existem na origem)
					))
							.toList();

					return new OpcoesGestaoIndicadorDto(
							gestao.getId(),
							gestao.getNome(),
							gestao.getAtiva(),
							gestao.getModelLabel(),
							labels);

				})
				.toList();

	}

}