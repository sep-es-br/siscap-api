package br.gov.es.siscap.dto;

import br.gov.es.siscap.enums.Permissoes;

import java.util.Set;

public record UsuarioDto(
        String token,
        String nome,
        String email,
        String subNovo,
        byte[] imagemPerfil,
        Set<Permissoes> permissoes) {
}
