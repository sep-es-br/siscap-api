package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.enums.edocs.ContextoIntegracaoEdocsEnum;
import br.gov.es.siscap.models.Pessoa;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncExecutorService {

    private final RelatoriosService relatoriosService;
    private final ProjetoService service;
    private final IntegraccaoEdocsService integracaoEdocsService;
    private final ProgramaProcessamentoService programaProcessamentoService;
    private final Logger logger = LogManager.getLogger(IntegraccaoEdocsService.class);

    @Async
    public void executarAutuacaoEdocs(Long idProjeto, Pessoa pessoa) {
        Resource resource = relatoriosService.gerarArquivo("DIC", idProjeto.intValue());
        String nomeArquivo = service.gerarNomeArquivo(idProjeto.intValue());
        integracaoEdocsService.assinarAutuarDespacharDicProccessoSUBCAP(resource, nomeArquivo, idProjeto.longValue(), pessoa);
    }

    @Async
    public void executarReentranhamentoDicEdocs(Long idProjeto, Pessoa pessoa) {
        Resource resource = relatoriosService.gerarArquivo("DIC", idProjeto.intValue());
        String nomeArquivo = service.gerarNomeArquivo(idProjeto.intValue());
        integracaoEdocsService.reentranharDespacharDicProccessoComplementacaoSUBCAP(resource, nomeArquivo,
                idProjeto.longValue(), pessoa);
    }

    @Async
    public void despacharProcessoOrgaoOrigemEdocs(Long idProjeto, List<ProjetoCamposComplementacaoDto> complementos, Pessoa pessoa) {
        integracaoEdocsService.despacharProccessoEdocsOrgaoOrigem(idProjeto.longValue(), complementos, pessoa);
    }

    @Async
    public void encerrarProcessoEdocs(ProjetoDto projetoDto) {
        integracaoEdocsService.encerrarProcessoEdocs(projetoDto);
    }

    @Async
    public void assinarCapturaParecerDIC(Long idProjeto, Long idParecer) {
        integracaoEdocsService.assinarCapturaParecerDIC(idProjeto, idParecer);
    }

    @Async
    public void entranharPareceresDIC(Long idProjeto) {
        integracaoEdocsService.entranharPareceresDIC(idProjeto);
    }

    @Async
    public void criarArquivoProgramaFaseAssinaturaEdocsServidor(Long idPrograma, List<String> subAssinantes,
            String nomeArquivo) {

        integracaoEdocsService.enviarArquivoAssinaturasPendentes(idPrograma, subAssinantes, nomeArquivo)
                .doOnSuccess(idDocumento -> 
                    programaProcessamentoService
                            .marcarCriacaoArquivoProgramaEdocs(
                                    idPrograma,
                                    subAssinantes,
                                    idDocumento)
                )
                .doOnError(e -> 
                    logger.error("Erro ao integrar com E-Docs para criar arquivo em fase assinatura. Programa {}",
                            idPrograma, e)
                )
                .subscribe();

    }

    @Async
    public void assinarArquivoFaseAssinaturaEdocsServidor(Long idPrograma, String idDocumentoCapturadoEdocs, String subAssinante ) {

        var chave = new ChaveEtapasIntegracao( idPrograma, ContextoIntegracaoEdocsEnum.PROGRAMA );

        integracaoEdocsService.assinarArquivoPendenteReativo( idPrograma, idDocumentoCapturadoEdocs, chave )
                .doOnSuccess( idDocumentoAutuado -> {
                    if (idDocumentoAutuado != null) {
                        programaProcessamentoService
                                .atualizarIdDocumentoEdocsNoPrograma(
                                        idPrograma,
                                        idDocumentoAutuado);
                    }
                    programaProcessamentoService
                            .marcarProgramaAssinado(
                                    idPrograma,
                                    subAssinante);
                    integracaoEdocsService.finalizaTodasEtapas(chave);
                })
                .doOnError(e -> 
                    logger.error("Erro ao integrar com E-Docs para assinar arquivo em fase assinatura. Programa {}",
                            idPrograma, e))
                .subscribe();
    }

    @Async
    public void autuarProgramaEdocs(ProgramaDto programaDto) {
        var chave = new ChaveEtapasIntegracao(programaDto.id(), ContextoIntegracaoEdocsEnum.PROGRAMA);
        integracaoEdocsService.autuarProgramaProjetoReativo(
                programaDto.id(),
                programaDto.idDocumentoCapturadoEdocs(), programaDto, chave)
                .doOnSuccess(ctx -> {
                    programaProcessamentoService
                            .marcarProgramaAutuadoEdocsEAvisoAutuado( programaDto,
                                    ctx.getProtocolo(), ctx.getIdProcesso() );
                    integracaoEdocsService.finalizaTodasEtapas(chave);
                })
                .subscribe();
    }

    @Async
    public void recusarAssinaturaProgramaEdocs(Long idPrograma, String idDocumentoCapturadoEdocs,
            String subAssinante) {
        programaProcessamentoService
                .assinanteRecusouAssinarPrograma(
                        idPrograma,
                        subAssinante);
    }

}
