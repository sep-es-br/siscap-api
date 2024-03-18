package br.gov.es.siscap.dto;

public record UsuarioDto(
        String nome,
        String email,
        byte[] imagemPerfil) {
}
