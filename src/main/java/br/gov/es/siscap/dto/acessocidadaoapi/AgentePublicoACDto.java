package br.gov.es.siscap.dto.acessocidadaoapi;

public record AgentePublicoACDto(
        String sub,
        String subDescontinuado,
        String nome,
        String apelido,
        String email) {
    public AgentePublicoACDto(AgentePublicoACResponseDto dto) {
        this(dto.Sub(), dto.SubDescontinuado(), dto.Nome(), dto.Apelido(), dto.Email());
    }

    public record AgentePublicoACResponseDto(
            String Sub,
            String SubDescontinuado,
            String Nome,
            String Apelido,
            String Email) {
    }
}

