package br.gov.es.siscap.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

public record OrganizacaoForm(
			@NotBlank
			String nome,

			@NotBlank
			String abreviatura,
			String telefone,
			String cnpj,
			String email,
			String site,
			MultipartFile imagemPerfil,
			Long idOrganizacaoPai,
			@NotNull
			Long idPessoaResponsavel,
			Long idCidade,
			Long idEstado,
			@NotNull
			Long idPais,
			@NotNull
			Long idTipoOrganizacao) {
}
