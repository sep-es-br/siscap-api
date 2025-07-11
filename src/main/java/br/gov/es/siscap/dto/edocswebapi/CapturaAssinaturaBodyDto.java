package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CapturaAssinaturaBodyDto(
    
    @JsonProperty("idPapelCapturadorAssinante")
    String idPapelCapturadorAssinante,
    
    @JsonProperty("idClasse")
    String idClasse,
    
    @JsonProperty("nomeArquivo")
    String nomeArquivo,
    
    @JsonProperty("credenciarCapturador")
    boolean credenciarCapturador,
    
    @JsonProperty("restricaoAcesso")
    RestricaoAcessoBodyDto restricaoAcessoBodyDto,

    @JsonProperty("identificadorTemporarioArquivoNaNuvem")
    String identificadorTemporarioArquivoNaNuvem

) {}
