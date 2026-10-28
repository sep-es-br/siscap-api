
package br.gov.es.siscap.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class DespachoAssinanteTemplateService {

    private static final Logger logger =
            LogManager.getLogger(DespachoAssinanteTemplateService.class);

    /**
     * Identifica variáveis no formato:
     *
     * {{programa}}
     * {{siglasProjetos}}
     * {{orgaoProponente}}
     * {{valorEstimado}}
     * {{numeroParecerElegibilidade}}
     *
     * Também aceita espaços:
     *
     * {{ programa }}
     */
    private static final Pattern PADRAO_VARIAVEL =
            Pattern.compile("\\{\\{\\s*([a-zA-Z][a-zA-Z0-9]*)\\s*\\}\\}");

    /**
     * Substitui as variáveis presentes no modelo do despacho
     * pelos valores fornecidos no mapa.
     *
     * Variáveis desconhecidas ou sem valor permanecem
     * no texto para facilitar a identificação de pendências.
     *
     * @param textoModelo Texto original cadastrado no banco.
     * @param variaveis Mapa com os valores das variáveis.
     * @return Texto do despacho após as substituições.
     */
    public String processar(
            String textoModelo,
            Map<String, String> variaveis) {

        if (textoModelo == null || textoModelo.isBlank()) {
            return "";
        }

        Map<String, String> valores =
                variaveis != null
                        ? variaveis
                        : Collections.emptyMap();

        Matcher matcher = PADRAO_VARIAVEL.matcher(textoModelo);

        StringBuilder resultado = new StringBuilder();

        Set<String> variaveisPendentes = new HashSet<>();

        while (matcher.find()) {

            String nomeVariavel = matcher.group(1);

            String valor = valores.get(nomeVariavel);

            if (valor == null || valor.isBlank()) {

                variaveisPendentes.add(nomeVariavel);

                // Mantém o marcador original no texto.
                // Exemplo: {{numeroParecerElegibilidade}}
                matcher.appendReplacement(
                        resultado,
                        Matcher.quoteReplacement(matcher.group())
                );

                continue;
            }

            matcher.appendReplacement(
                    resultado,
                    Matcher.quoteReplacement(valor)
            );
        }

        matcher.appendTail(resultado);

        if (!variaveisPendentes.isEmpty()) {
            logger.warn(
                    "Despacho contém variáveis não resolvidas: {}",
                    variaveisPendentes
            );
        }

        return resultado.toString();
    }

    /**
     * Identifica todas as variáveis presentes em um modelo.
     *
     * Pode ser utilizado futuramente para validar
     * os modelos cadastrados no sistema.
     */
    public Set<String> identificarVariaveis(String textoModelo) {

        if (textoModelo == null || textoModelo.isBlank()) {
            return Collections.emptySet();
        }

        Set<String> variaveis = new HashSet<>();

        Matcher matcher = PADRAO_VARIAVEL.matcher(textoModelo);

        while (matcher.find()) {
            variaveis.add(matcher.group(1));
        }

        return variaveis;
    }

    /**
     * Identifica quais variáveis do modelo ainda não possuem
     * valores disponíveis.
     */
    public Set<String> identificarVariaveisPendentes(
            String textoModelo,
            Map<String, String> variaveis) {

        Set<String> encontradas = identificarVariaveis(textoModelo);

        Map<String, String> valores =
                variaveis != null
                        ? variaveis
                        : Collections.emptyMap();

        Set<String> pendentes = new HashSet<>();

        for (String variavel : encontradas) {

            String valor = valores.get(variavel);

            if (valor == null || valor.isBlank()) {
                pendentes.add(variavel);
            }
        }

        return pendentes;
        
    }

}
