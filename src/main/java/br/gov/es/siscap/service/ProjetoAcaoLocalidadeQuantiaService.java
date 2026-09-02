package br.gov.es.siscap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.gov.es.siscap.dto.RateioDto;
import br.gov.es.siscap.models.ProjetoAcao;
import br.gov.es.siscap.models.ProjetoAcaoLocalidadeQuantia;
import br.gov.es.siscap.repository.ProjetoAcaoLocalidadeQuantiaRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoAcaoLocalidadeQuantiaService {

    private final ProjetoAcaoLocalidadeQuantiaRepository repository;

    public List<ProjetoAcaoLocalidadeQuantia> cadastrar(
            ProjetoAcao projetoAcao,
            List<RateioDto> rateios) {

        if (rateios == null || rateios.isEmpty()) {
            return List.of();
        }

        List<ProjetoAcaoLocalidadeQuantia> localidades = rateios.stream()
                .map(rateio -> new ProjetoAcaoLocalidadeQuantia(
                        projetoAcao,
                        rateio))
                .toList();

        return repository.saveAll(localidades);

    }

    @Transactional
    public void atualizar(
            ProjetoAcao projetoAcao,
            List<RateioDto> rateiosDto) {

        List<RateioDto> rateios = Optional.ofNullable(rateiosDto)
                .orElseGet(List::of);

        Map<Long, RateioDto> rateiosRecebidos = rateios.stream()
                .collect(Collectors.toMap(
                        RateioDto::idLocalidade,
                        Function.identity(),
                        (rateio1, rateio2) -> {
                            throw new IllegalArgumentException(
                                    "Localidade duplicada no rateio da ação: "
                                            + rateio1.idLocalidade());
                        }));

        List<ProjetoAcaoLocalidadeQuantia> rateiosAtuais = repository.findByProjetoAcao(projetoAcao);

        Map<Long, ProjetoAcaoLocalidadeQuantia> rateiosAtuaisPorLocalidade = rateiosAtuais.stream()
                .collect(Collectors.toMap(
                        rateio -> rateio.getLocalidade().getId(),
                        Function.identity()));

        List<ProjetoAcaoLocalidadeQuantia> rateiosParaSalvar = new ArrayList<>();

        rateiosRecebidos.forEach((idLocalidade, rateioDto) -> {

            ProjetoAcaoLocalidadeQuantia rateio = rateiosAtuaisPorLocalidade.remove(idLocalidade);

            if (rateio == null) {
                rateio = new ProjetoAcaoLocalidadeQuantia(
                        projetoAcao,
                        rateioDto);
            } else {
                rateio.atualizar(rateioDto);
            }

            rateiosParaSalvar.add(rateio);
        });

        // Tudo que sobrou existia no banco,
        // mas não veio mais do front.
        rateiosAtuaisPorLocalidade.values()
                .forEach(repository::delete);

        repository.saveAll(rateiosParaSalvar);
        
    }

}
