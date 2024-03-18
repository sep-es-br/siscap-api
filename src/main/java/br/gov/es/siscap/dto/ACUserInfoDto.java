package br.gov.es.siscap.dto;

public record ACUserInfoDto(
        String apelido,
        String cpfValidado,
        String verificada,
        String verificacaoTipo,
        String subNovo,
        String agentepublico,
        String email,
        String sub) {
}
