package br.gov.es.siscap.form;

import org.springframework.web.multipart.MultipartFile;

public record OrganizacaoUpdateForm(
        String nome,
        String abreviatura,
        String telefone,
        String cnpj,
        String email,
        String site,
        MultipartFile imagemPerfil,
        Long idOrganizacaoPai,
        Long idPessoaResponsavel,
        Long idCidade,
        Long idEstado,
        Long idPais,
        Long idTipoOrganizacao) {
}
