package br.gov.es.siscap.dto.acessocidadaoapi;

public record EmailSubResponseDto(
                String email,
                String corporativo) {

        public EmailSubResponseDto(EmailSubResponseDto responseDto) {
                this(responseDto.email(),
                                responseDto.corporativo());
        }
}
