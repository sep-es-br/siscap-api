package br.gov.es.siscap.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record PessoaUpdateForm(
        String nome,
        String nomeSocial,
        String nacionalidade,
        String genero,
        @Size(max = 11, min = 11)
        String cpf,
        @Email
        String email,
        String telefoneComercial,
        String telefonePessoal,
        EnderecoForm endereco,
        MultipartFile imagemPerfil
) {
}
