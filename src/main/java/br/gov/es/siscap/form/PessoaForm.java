package br.gov.es.siscap.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record PessoaForm(
        @NotBlank
        String nome,
        String nomeSocial,
        @NotBlank
        String nacionalidade,
        @NotBlank
        String genero,
        @Size(max = 11, min = 11)
        String cpf,
        @Email
        @NotBlank
        String email,
        String telefoneComercial,
        String telefonePessoal,
        @Valid
        EnderecoForm endereco,
        Long idOrganizacao,
        String sub,
        Set<Long> idAreasAtuacao,
        MultipartFile imagemPerfil) {
}
