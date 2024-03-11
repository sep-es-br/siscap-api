package br.gov.es.siscap.form;

import org.springframework.web.multipart.MultipartFile;

public record EntidadeUpdateForm(
        String nome,
        String abreviatura,
        String telefone,
        String cnpj,
        String fax,
        String email,
        String site,
        MultipartFile imagemPerfil,
        Long idEntidadePai,
        Long idPessoaResponsavel,
        Long idCidade,
        Long idPais,
        Long idTipoEntidade) {
}
