package br.gov.es.siscap.dto.edocswebapi;

import br.gov.es.siscap.dto.ProjetoDto;
import lombok.Data;

@Data
public class FluxoContextoIntegracaoDto {

    private final ProjetoDto projeto;
    private final String token;
    private String idEventoAvocar;
    private String idEventoCaptura;
    private String idEventoDespachar;
    private String idEventoAutuar;
    private String idEventoEntranhamento;
    private String IdEventoDesentranhar;
    private String idDocumento;
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

}
