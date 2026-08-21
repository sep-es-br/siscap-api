package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.IndicadorMetaRelatorioDto;
import br.gov.es.siscap.dto.IndicadorPentahoBiDto;
import br.gov.es.siscap.dto.PlanejamentoDetalhamentoRelatorioDto;
import br.gov.es.siscap.dto.ProjetoAcoesPlanejamentoProjetoRelatorio;
import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoMetaDto;
import br.gov.es.siscap.dto.ProjetoIndicadorCatalogoMetaDto;
import br.gov.es.siscap.dto.ProjetoIndicadorDto;
import br.gov.es.siscap.dto.ProjetoIndicadoresRelatorio;
import br.gov.es.siscap.dto.ProjetoOdsDto;
import br.gov.es.siscap.dto.ProjetoOdsRelatorioDto;
import br.gov.es.siscap.dto.indicadoresexternos.FiltroIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesIndicadoresDto;
import br.gov.es.siscap.enums.ExibirMarcaDaguaProgramaEnum;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelatoriosService {

	@Value("${api.parecer.guidSUBEPP}")
	private String guidSUBEPP;

	@Value("${api.parecer.guidSUBEO}")
	private String guidSUBEO;

	@Value("${api.edocs.guiddestinoSUBCAP}")
	private String guidSUBCAP;

	@Value("${frontend.edocs.host}")
	private String edocsBaseUrl;

	@Value("${raiz.relatorios}")
	private String raizRelatorios;

	private final DataSource dataSource;

	private final IndicadorExternoService indicadorBIService;

	private final Logger logger = LogManager.getLogger(RelatoriosService.class);

	public Resource gerarArquivo(String nomeArquivo, Integer idProjeto, ExibirMarcaDaguaProgramaEnum exibirMarcaDagua,
			ProjetoDto projetoDto) {

		JasperPrint jasperPrint = preencherArquivo(recuperarArquivo(nomeArquivo), idProjeto, exibirMarcaDagua,
				projetoDto);

		return exportarRelatorio(jasperPrint);
	}

	public Resource gerarArquivoParecerDIC(String nomeArquivo, Long idProjeto, Long idParecer,
			String descricaoTipoParecer, Boolean elegivel) {
		JasperPrint jasperPrint = preencherArquivoParecer(recuperarArquivo(nomeArquivo), idProjeto, idParecer,
				descricaoTipoParecer, elegivel);
		return exportarRelatorio(jasperPrint);
	}

	private InputStream recuperarArquivo(String nomeArquivo) {
		try {
			return new ClassPathResource(raizRelatorios + "/" + nomeArquivo + ".jasper").getInputStream();
		} catch (IOException e) {
			logger.info("Erro ao encontrar o arquivo {}.jasper, local {}", nomeArquivo, raizRelatorios);
			throw new SiscapServiceException(List.of("Erro ao encontrar o arquivo " + nomeArquivo + ".jasper"
					+ " local do arquivo : " + raizRelatorios));
		}
	}

	private JasperPrint preencherArquivo(InputStream relatorio,
			Integer idProjeto,
			ExibirMarcaDaguaProgramaEnum exibirMarcaDagua,
			ProjetoDto projetoDto) {

		try {

			String marca = Optional.ofNullable(exibirMarcaDagua)
					.map(e -> e.getValue())
					.map(String::trim)
					.orElse("N");

			HashMap<String, Object> map = new HashMap<>();

			List<ProjetoOdsRelatorioDto> listaOdsProjeto = projetoDto.odsProjeto()
					.stream()
					.sorted(Comparator.comparing(ProjetoOdsDto::odsOrdem))
					.map(o -> new ProjetoOdsRelatorioDto(
							o.idOdsProjeto(),
							o.odsId(),
							o.odsOrdem(),
							o.odsNome(),
							o.odsDescricao(),
							o.odsCor()))
					.toList();

			List<Integer> idsIndicadoresBI = projetoDto.indicadoresProjeto()
					.stream()
					.map(ProjetoIndicadorDto::idIndicadorExterno)
					.distinct()
					.toList();

			FiltroIndicadorDto filtroIdIndicadores = new FiltroIndicadorDto(
					null,
					List.of(),
					List.of(),
					idsIndicadoresBI);

			Map<Integer, List<IndicadorPentahoBiDto>> indicadoresBIMap = indicadorBIService
					.listarIndicadoresBI(filtroIdIndicadores)
					.stream()
					.collect(Collectors.groupingBy(IndicadorPentahoBiDto::idIndicador));

			List<ProjetoIndicadoresRelatorio> listaIndicadoresBI = projetoDto.indicadoresProjeto()
					.stream()
					.map(indicador -> {

						List<IndicadorPentahoBiDto> linhasIndicador = indicadoresBIMap
								.get(indicador.idIndicadorExterno());

						if (linhasIndicador == null || linhasIndicador.isEmpty()) {
							return null;
						}

						IndicadorPentahoBiDto indicadorBI = linhasIndicador.get(0);

						List<IndicadorMetaRelatorioDto> listaMetasIndicador = indicador.metasIndicadorProjeto()
								.stream()
								.sorted(Comparator.comparing(ProjetoIndicadorCatalogoMetaDto::anoMeta))
								.map(meta -> new IndicadorMetaRelatorioDto(
										meta.anoMeta(),
										meta.valorMeta()))
								.toList();

						String metasFormatadas = indicador.metasIndicadorProjeto()
								.stream()
								.sorted(Comparator.comparing(ProjetoIndicadorCatalogoMetaDto::anoMeta))
								.map(meta -> meta.anoMeta() + " (" + meta.valorMeta() + ")")
								.collect(Collectors.joining(" • "));

						String baseReferencia = String.format("%s (%s)", indicadorBI.maiorAnoIndicador(),
								indicadorBI.maiorMetaIndicador());

						return new ProjetoIndicadoresRelatorio(
								indicadorBI.nomeIndicador(),
								indicadorBI.unidadeMedida(),
								null,
								indicadorBI.medidoPor(),
								baseReferencia,
								null,
								metasFormatadas,
								false,
								listaMetasIndicador);

					})
					.filter(Objects::nonNull)
					.toList();

			List<ProjetoIndicadoresRelatorio> listaIndicadoresAvulsosProjeto = projetoDto.indicadoresAvulsosProjeto()
					.stream()
					.map(indicador -> new ProjetoIndicadoresRelatorio(
							indicador.indicadorAvulso().nomeIndicador(),
							indicador.indicadorAvulso().unidadeMedida(),
							indicador.indicadorAvulso().fonteIndicador(),
							indicador.indicadorAvulso().medidoPor(),
							indicador.indicadorAvulso().baseDeReferencia(),
							indicador.indicadorAvulso().formulaCalculo(),
							indicador.metasIndicadorProjeto().stream()
									.sorted(Comparator.comparing(ProjetoIndicadorAvulsoMetaDto::anoMeta))
									.map(meta -> meta.anoMeta() + " (" + meta.valorMeta() + ")")
									.collect(Collectors.joining(" • ")),
							true,
							indicador.metasIndicadorProjeto().stream()
									.sorted(Comparator.comparing(ProjetoIndicadorAvulsoMetaDto::anoMeta))
									.map(meta -> new IndicadorMetaRelatorioDto(
											meta.anoMeta(),
											meta.valorMeta()))
									.toList()))
					.toList();

			List<ProjetoIndicadoresRelatorio> listaIndicadoresProjetoFinal = new ArrayList<>();

			listaIndicadoresProjetoFinal.addAll(listaIndicadoresAvulsosProjeto);
			listaIndicadoresProjetoFinal.addAll(listaIndicadoresBI);

			List<ProjetoAcoesPlanejamentoProjetoRelatorio> listaPlanejamentoAcoesProjeto = projetoDto
					.acoesPlanejamentoProjeto()
					.stream()
					.map(acao -> new ProjetoAcoesPlanejamentoProjetoRelatorio(
							projetoDto.naoPrevistoNoPpa(),
							"",
							acao.codAcao(),
							acao.acaoPpaLoa().nomeAcao(),
							acao.acaoPpaLoa().nomeUnidadeOrcamentaria(),
							acao.acaoPpaLoa().nomeOrgao(),
							acao.acaoPpaLoa().nomeFuncao(),
							acao.acaoPpaLoa().nomePrograma(),
							acao.acaoPpaLoa().valorPpa(),
							acao.acaoPpaLoa().anoAcao(),
							acao.acaoPpaLoa().valorLoa(),
							acao.acaoPpaLoa().detalhamentosLoa().stream()
									.map(detalhamento -> new PlanejamentoDetalhamentoRelatorioDto(
											detalhamento.codigoGnd(),
											detalhamento.codigoModalidade(),
											detalhamento.idUso(),
											detalhamento.fonte(),
											detalhamento.valor()))
									.toList()))
					.toList();

			map.put("idProjeto", idProjeto);
			map.put("pathRelatorios", raizRelatorios);
			map.put("exibirMarcaDagua", marca);
			map.put("odsProjetoDataSource", new JRBeanCollectionDataSource(listaOdsProjeto));
			map.put("indicadoresDataSource", new JRBeanCollectionDataSource(listaIndicadoresProjetoFinal));
			map.put("planejamentoDataSource", new JRBeanCollectionDataSource(listaPlanejamentoAcoesProjeto));
			map.put("naoPrevistoPpa", projetoDto.naoPrevistoNoPpa());
			map.put("periodoPlanejamento", "");

			map.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));

			return JasperFillManager.fillReport(relatorio, map, dataSource.getConnection());

		} catch (JRException | SQLException e) {
			logger.error("Erro ao preencher o relatório.", e);
			throw new SiscapServiceException(List.of("Erro ao preencher o relatório. Contate o suporte."));
		}

	}

	private JasperPrint preencherArquivoParecer(InputStream relatorio, Long idProjeto, Long idParecer,
			String descricaoTipoParecer, Boolean elegivel) {
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("idProjeto", idProjeto);
			map.put("pathRelatorios", raizRelatorios);
			map.put("idParecer", idParecer);
			map.put("descricaoTipoParecer", descricaoTipoParecer);
			map.put("elegivel", elegivel);
			map.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
			return JasperFillManager.fillReport(relatorio, map, dataSource.getConnection());
		} catch (JRException | SQLException e) {
			logger.info("Erro ao preencher o pdf do parecer.");
			throw new SiscapServiceException(List.of("Erro ao preencher o pdf do parecer. Contate o suporte."));
		}
	}

	private Resource exportarRelatorio(JasperPrint jasperPrint) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
			return new ByteArrayResource(byteArrayOutputStream.toByteArray());
		} catch (JRException e) {
			logger.info("Erro ao exportar o relatório.");
			throw new SiscapServiceException(List.of("Erro ao exportar o relatório. Contate o suporte."));
		}
	}

	public Resource gerarArquivoPrograma(String nomeArquivo, Integer idPrograma,
			ExibirMarcaDaguaProgramaEnum exibirMarcaDagua) {
		JasperPrint jasperPrint = preencherArquivoPrograma(recuperarArquivo(nomeArquivo), idPrograma, exibirMarcaDagua);
		return exportarRelatorio(jasperPrint);
	}

	private JasperPrint preencherArquivoPrograma(InputStream relatorio, Integer idPrograma,
			ExibirMarcaDaguaProgramaEnum exibirMarcaDagua) {

		String marca = Optional.ofNullable(exibirMarcaDagua)
				.map(e -> e.getValue())
				.map(String::trim)
				.orElse("N");

		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("idPrograma", idPrograma);
			map.put("pathRelatorios", raizRelatorios);
			map.put("guidSUBEPP", guidSUBEPP);
			map.put("guidSUBEO", guidSUBEO);
			map.put("guidSUBCAP", guidSUBCAP);
			map.put("edocsBaseUrl", edocsBaseUrl);
			map.put("exibirMarcaDagua", marca);
			map.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
			return JasperFillManager.fillReport(relatorio, map, dataSource.getConnection());
		} catch (JRException | SQLException e) {
			logger.info("Erro ao preencher o relatório.");
			throw new SiscapServiceException(List.of("Erro ao preencher o relatório. Contate o suporte."));
		}

	}

}
