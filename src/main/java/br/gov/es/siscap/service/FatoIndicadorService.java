package br.gov.es.siscap.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.siscap.dto.indicadoresexternos.IndicadorFatoAgrupadoDTO;
import br.gov.es.siscap.dto.indicadoresexternos.MetasIndicadorExternoDto;
import br.gov.es.siscap.models.IndicadorExterno;
import br.gov.es.siscap.models.IndicadorFatoExterno;
import br.gov.es.siscap.repository.IndicadorFatoExternoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FatoIndicadorService {

        private final IndicadorFatoExternoRepository fatoIndicadorRepository;

        public Map<Integer, IndicadorFatoAgrupadoDTO> buscarDadosAgrupados(List<IndicadorExterno> indicadores) {

                if (indicadores == null || indicadores.isEmpty()) {
                        return Map.of();
                }

                // 1. Extrai IDs
                List<Integer> ids = indicadores.stream()
                                .map(IndicadorExterno::getId)
                                .toList();

                // 2. Busca tudo em uma query
                List<IndicadorFatoExterno> fatos = fatoIndicadorRepository.findByIndicadorIds(ids);

                // 3. Agrupa por indicador
                Map<Integer, List<IndicadorFatoExterno>> agrupado = fatos.stream()
                                .collect(Collectors.groupingBy(f -> f.getIndicador().getId()));

                // 4. Monta resultado final
                Map<Integer, IndicadorFatoAgrupadoDTO> resultado = new HashMap<>();

                for (Map.Entry<Integer, List<IndicadorFatoExterno>> entry : agrupado.entrySet()) {

                        Integer indicadorId = entry.getKey();
                        List<IndicadorFatoExterno> lista = entry.getValue();

                        // metas (lista)
                        List<MetasIndicadorExternoDto> metas = lista.stream()
                                        .map(f -> new MetasIndicadorExternoDto(
                                                        f.getAno(),
                                                        f.getValorMeta()))
                                        .toList();

                        // 👇 pega da primeira linha (já vem pronto do banco)
                        IndicadorFatoExterno primeiro = lista.get(0);

                        Integer maiorAno = primeiro.getMaiorAnoIndicador();
                        BigDecimal maiorMeta = primeiro.getMaiorMetaIndicador();

                        resultado.put(  indicadorId,
                                        new IndicadorFatoAgrupadoDTO(
                                                        metas,
                                                        maiorAno,
                                                        maiorMeta));

                }

                return resultado;
        }

}
