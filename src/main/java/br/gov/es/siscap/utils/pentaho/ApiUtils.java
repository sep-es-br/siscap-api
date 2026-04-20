package br.gov.es.siscap.utils.pentaho;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class ApiUtils extends PentahoBIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiUtils.class);

    public <T> List<T> consult(
            String target,
            String dataAccessId,
            String pmoPath,
            Map<String, Object> params,
            Function<Map<String, JsonNode>, T> mapper) {

        try {

            String result = doRequest(buildEndpointUri(pmoPath, target, dataAccessId, params));

            // Corrigir as entidades HTML
            result = StringEscapeUtils.unescapeHtml4(result);

            List<Map<String, JsonNode>> resultset = extractDataFromResponse(result);

            return resultset.stream().map(mapper).toList();

        } catch (Exception e) {
            LOGGER.error("Error during consult: ", e);
            return List.of();
        }
    }

    @Override
    protected String buildEndpointUri(String pmoPath, String target, String dataAccess, Map<String, Object> params) {
        return super.buildEndpointUri(pmoPath, target, dataAccess, params);
    }

    @Override
    protected String buildEndpointUri(String target, String dataAccess, Map<String, Object> params) {
        return super.buildEndpointUri(null,target, dataAccess, params);
    }

}
