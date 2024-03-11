package br.gov.es.siscap.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record EntidadeForm(
        @NotBlank
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
        @NotNull
        Long idPais,
        @NotNull
        Long idTipoEntidade) {
}
