package br.gov.es.siscap.dto.edocswebapi;

import java.util.List;

import br.gov.es.siscap.dto.ProjetoDto;
import lombok.Data;

@Data
public class FluxoContextoIntegracaoDto {

    private ProjetoDto projeto;
    private final String token;
    private String[] idDocumentos;

    private String idEventoAvocar;
    private String idEventoCaptura;
    private String idEventoDespachar;
    private String idEventoAutuar;
    private String idEventoEntranhamento;
    private String IdEventoDesentranhar;
    
    private String idProcesso;
    private String protocolo;
    private String idDocumentoDesentranhar;
    private String idEventoEncerramento;

    private SituacaoEventoDto situacaoEventoAvocarDto;
    private SituacaoEventoDto situacaoEventoEntranhamentoDto;

    private GerarUrlUploadResponseDto dtoUploadArquivoResponse;
    private ProcessoVinculadoDocumentoDto dtoProcessoVinculadoDocumento;
    private AtosProcessoEdocsDto dtoAtoProcessoDocs;

    private ProcessoDocumentosAtoProcessoDto documentoAtoProcessoDto;

    private List<String> assinantes;

    public FluxoContextoIntegracaoDto(ProjetoDto projeto, String token ) {
        this.projeto = projeto;
        this.token = token;
    }

    public FluxoContextoIntegracaoDto(ProjetoDto projeto, String token, String[] idDocumentos) {
        this.projeto = projeto;
        this.token = token;
        this.idDocumentos = idDocumentos;
    }

    public FluxoContextoIntegracaoDto( String token, List<String> assinantes ) {
        this.token = token;
        this.assinantes = assinantes;
    }

}
