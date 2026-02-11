package br.gov.es.siscap.dto.edocswebapi;

import java.util.List;

import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.service.ChaveEtapasIntegracao;
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
    private String idDocumentoAssinarFaseAssinatura;
    private Long idPrograma;
    private ProgramaDto programaDto;
    private String idEventoAssinatura;

    private SituacaoEventoDto situacaoEventoAto;

    private ChaveEtapasIntegracao chaveContextoIntegracao;

    public FluxoContextoIntegracaoDto( ProjetoDto projeto, String token, ChaveEtapasIntegracao chaveContextoIntegracao ) {
        this.projeto = projeto;
        this.token = token;
        this.chaveContextoIntegracao = chaveContextoIntegracao;
    }

    public FluxoContextoIntegracaoDto(ProjetoDto projeto, String token, String[] idDocumentos) {
        this.projeto = projeto;
        this.token = token;
        this.idDocumentos = idDocumentos;
    }

    public FluxoContextoIntegracaoDto( String token, List<String> assinantes, ChaveEtapasIntegracao chaveContextoIntegracao ) {
        this.token = token;
        this.assinantes = assinantes;
        this.chaveContextoIntegracao = chaveContextoIntegracao;
    }

    public FluxoContextoIntegracaoDto( String token, Long idPrograma, String[] idDocumentos, ProgramaDto programaDto ) {
        this.token = token;
        this.idPrograma = idPrograma;
        this.idDocumentos = idDocumentos;
        this.programaDto = programaDto;
    }

    public FluxoContextoIntegracaoDto( String token, String idDocumentoFaseAssinatura, String idEventoAssinatura ) {
        this.token = token;
        this.idEventoAssinatura = idEventoAssinatura;
        this.idDocumentoAssinarFaseAssinatura = idDocumentoFaseAssinatura;
    }

}
