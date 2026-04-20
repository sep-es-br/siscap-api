package br.gov.es.siscap.utils.pentaho;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract class PentahoBIService {
    
	private final Logger logger = LogManager.getLogger(PentahoBIService.class);
    
    private static final String CHARSET = "UTF-8";

    @Value("${pentahoBI.baseURL}")
    private String baseURL;

    @Value("${pentahoBI.userId}")
    private String userId;

    @Value("${pentahoBI.password}")
    private String password;


    protected String buildEndpointUri(String path, String target, String dataAccessId, Map<String, Object> params) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.baseURL);

        HashMap<String, Object> allParams = new HashMap<>();
        allParams.put("path", path + target);
        allParams.put("dataAccessId", dataAccessId);
        if(params != null) allParams.putAll(params);

        List<String> paramPairs = allParams.entrySet().stream()
                                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                                    .toList();

        strBuilder.append(String.join("&", paramPairs));

        return strBuilder.toString();
    }

    protected abstract String buildEndpointUri(String target, String dataAccess, Map<String, Object> params);


    protected  String doRequest(String uri) throws Exception{
        
        String notEncoded = userId + ":" + password;
        String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodedAuth);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName(CHARSET)));

        return restTemplate.exchange(RequestEntity.get(new URI(uri)).headers(headers).build(), String.class).getBody();
    }

    protected  List<Map<String, JsonNode>> extractDataFromResponse(String json) {
        ArrayList<Map<String, JsonNode>> lista = new ArrayList<>();

        try {
            JsonNode root = new ObjectMapper().readTree(json);

            ArrayNode metadata = (ArrayNode) root.get("metadata");
            ArrayList<String> labels = new ArrayList<>();
            
            metadata.forEach(node -> {
                labels.add(node.get("colName").asText());
            });

            ArrayNode resultset = (ArrayNode) root.get("resultset");

            resultset.forEach(node -> {
                ArrayNode datas = (ArrayNode) node;
                HashMap<String, JsonNode> map = new HashMap<>();
                for(int i = 0; i < datas.size(); i++) {
                    map.put(labels.get(i), datas.get(i));
                }
                lista.add(map);
            });

        } catch(Exception e) {
            logger.error(e);
        }
        return lista;
    }

}