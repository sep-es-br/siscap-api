package br.gov.es.siscap.service;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProjetoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContextoDespachoProgramaService {

    private final ProjetoRepository projetoRepository;

    /**
     * Monta o contexto de variáveis utilizado nos
     * modelos de despacho de assinatura do Programa.
     *
     * @param programa Programa que será autorizado.
     * @return Mapa contendo as variáveis e seus valores.
     */
    public Map<String, String> montar(Programa programa) {

        Objects.requireNonNull(
            programa,
            "Programa não pode ser nulo."
        );

        // Recupera os projetos vinculados ao Programa.
        // Este método deverá ser confirmado no repository.
        // List<Projeto> projetosVinculados =
        //     projetoRepository.findProjetosVinculadosAoPrograma(
        //         programa.getId()
        //     );

        List<Projeto> projetosVinculados = projetoRepository.findAllByPrograma(programa).stream().collect(null);

        Map<String, String> variaveis = new HashMap<>();

        // Título do Programa
        variaveis.put(
            "programa",
            textoOuVazio(programa.getTitulo())
        );

        // Siglas dos projetos vinculados
        variaveis.put(
            "siglasProjetos",
            obterSiglasProjetos(projetosVinculados)
        );

        // Organizações proponentes dos projetos
        variaveis.put(
            "orgaoProponente",
            obterOrgaosProponentes(projetosVinculados)
        );

        // Valor consolidado do Programa, incluindo
        // o percentual de despesas administrativas
        variaveis.put(
            "valorEstimado",
            obterValorEstimado(programa)
        );

        // TODO:
        // Identificar de onde vem o número do Parecer
        // de Elegibilidade.
        //
        // Por enquanto, não adicionamos essa variável.
        // O processador deverá preservar o marcador
        // {{numeroParecerElegibilidade}} quando ausente.

        return variaveis;
    }

    /**
     * Retorna as siglas dos projetos vinculados,
     * eliminando duplicações e ordenando alfabeticamente.
     */
    private String obterSiglasProjetos(
            List<Projeto> projetos) {

        if (projetos == null || projetos.isEmpty()) {
            return "";
        }

        return projetos.stream()
            .map(Projeto::getSigla)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(sigla -> !sigla.isBlank())
            .distinct()
            .sorted()
            .collect(Collectors.joining(", "));
    }

    /**
     * Retorna os órgãos proponentes dos projetos
     * vinculados ao Programa.
     *
     * Se mais de um projeto pertencer à mesma
     * organização, o nome aparecerá somente uma vez.
     */
    private String obterOrgaosProponentes(
            List<Projeto> projetos) {

        if (projetos == null || projetos.isEmpty()) {
            return "";
        }

        return projetos.stream()
            .map(Projeto::getOrganizacao)
            .filter(Objects::nonNull)
            .map(Organizacao::getNome)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(nome -> !nome.isBlank())
            .distinct()
            .sorted()
            .collect(Collectors.joining(", "));
    }

    /**
     * Retorna o valor total calculado do Programa,
     * incluindo o percentual de despesas administrativas.
     *
     * O valor já é consolidado pelo SISCAP.
     * Portanto, não recalculamos os valores das ações.
     */
    private String obterValorEstimado(
            Programa programa) {

        BigDecimal valor = programa.getValorCalculadoTotal();

        if (valor == null) {
            return "";
        }

        NumberFormat formatoMoeda =
            NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("pt-BR")
            );

        return formatoMoeda.format(valor);
    }

    /**
     * Retorna uma string vazia quando o valor
     * informado é nulo.
     */
    private String textoOuVazio(String valor) {
        return valor == null ? "" : valor.trim();
    }
}

