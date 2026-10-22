package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProgramaDicDto;
import br.gov.es.siscap.dto.ProgramaInformacoesDicsDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.models.Localidade;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProjetoRepository;
import java.text.Collator;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramaInformacoesDicsService {
    private final ProjetoRepository projetoRepository;

    public ProgramaInformacoesDicsDto buscar(List<Long> idsProjetos) {
        List<Long> ids = idsProjetos.stream().distinct().toList();
        Map<Long, Projeto> projetos = projetoRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Projeto::getId, Function.identity()));
        if (projetos.size() != ids.size()) {
            throw new ValidacaoSiscapException(List.of("Um dos DICs informados não foi encontrado."));
        }

        List<ProgramaDicDto> dics = ids.stream().map(projetos::get)
                .map(projeto -> new ProgramaDicDto(projeto.getId(),
                        projeto.getSigla() + " - " + projeto.getTitulo(), projeto.getObjetivo()))
                .toList();

        Map<Long, Localidade> localidades = new LinkedHashMap<>();
        projetos.values().stream().flatMap(this::localidadesDoProjeto)
                .forEach(localidade -> localidades.putIfAbsent(localidade.getId(), localidade));
        Collator collator = Collator.getInstance(Locale.forLanguageTag("pt-BR"));
        List<OpcoesDto> regioes = localidades.values().stream()
                .map(localidade -> new OpcoesDto(localidade.getId(),
                        "Estado".equals(localidade.getTipo()) ? "Todo o Estado" : localidade.getNome()))
                .sorted(Comparator.comparing(OpcoesDto::nome, collator).thenComparing(OpcoesDto::id))
                .toList();
        return new ProgramaInformacoesDicsDto(dics, regioes);
    }

    private Stream<Localidade> localidadesDoProjeto(Projeto projeto) {
        if (!projeto.getProjetoAcoesSet().isEmpty()) {
            return projeto.getProjetoAcoesSet().stream()
                    .flatMap(acao -> acao.getRateios().stream())
                    .map(rateio -> rateio.getLocalidade());
        }
        // DICs anteriores ao rateio por ação ainda podem possuir rateio no projeto.
        return projeto.getLocalidadeQuantiaSet().stream().map(rateio -> rateio.getLocalidade());
    }
}
