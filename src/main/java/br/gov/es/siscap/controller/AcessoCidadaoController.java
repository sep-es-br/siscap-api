package br.gov.es.siscap.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Base64;

@RestController
@RequestMapping("/signin")
public class AcessoCidadaoController {

    @Value("${frontend.host}")
    private String frontEndHost;

    /**
     * Endpoint necessário para fazer o redirecionamento do token de acesso para o front end.
     * Este endpoint recebe o redirecionamento a partir do arquivo acesso-cidadao-response.html que se fez necessário
     *
     * @param accessToken O access token do acesso cidadão.
     * @return Um redirect para o frontend passando o token codigicado.
     */
    @GetMapping("/acesso-cidadao-response")
    public RedirectView acessoCidadaoResponse(String accessToken) {
        String token = Base64.getEncoder().encodeToString(accessToken.getBytes());
        return new RedirectView(String.format("%s/siscap/token?token=%s", frontEndHost, token));
    }

}
