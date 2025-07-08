package br.gov.es.siscap.dto.edocswebapi;

public record CapturaAssinaturaBody(
    String idPapelCapturadorAssinante,
    String idClasse,
    String nomeArquivo,
    boolean credenciarCapturador,
    RestricaoAcessoBodyDto restricaoAcessoBodyDto
) {}
