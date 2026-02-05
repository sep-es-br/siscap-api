package br.gov.es.siscap.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.models.Programa;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsyncExecutorService {

    private final RelatoriosService relatoriosService;
    private final ProjetoService service;
    private final IntegraccaoEdocsService integracaoEdocsService;
    private final ProgramaProcessamentoService programaProcessamentoService;
    private final Logger logger = LogManager.getLogger(IntegraccaoEdocsService.class);

    @Async
    public void executarAutuacaoEdocs(Long idProjeto) {
        Resource resource = relatoriosService.gerarArquivo("DIC", idProjeto.intValue());
        String nomeArquivo = service.gerarNomeArquivo(idProjeto.intValue());
        integracaoEdocsService.assinarAutuarDespacharDicProccessoSUBCAP(resource, nomeArquivo, idProjeto.longValue());
    }

    @Async
    public void executarReentranhamentoDicEdocs(Long idProjeto) {
        Resource resource = relatoriosService.gerarArquivo("DIC", idProjeto.intValue());
        String nomeArquivo = service.gerarNomeArquivo(idProjeto.intValue());
        integracaoEdocsService.reentranharDespacharDicProccessoComplementacaoSUBCAP(resource, nomeArquivo,
                idProjeto.longValue());
    }

    @Async
    public void despacharProcessoOrgaoOrigemEdocs(Long idProjeto, List<ProjetoCamposComplementacaoDto> complementos) {
        integracaoEdocsService.despacharProccessoEdocsOrgaoOrigem(idProjeto.longValue(), complementos);
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
    public void criarArquivoProgramaFaseAssinaturaEdocsServidor(Long idPrograma, List<String> assinantes,
            String nomeArquivo) {
        integracaoEdocsService.enviarArquivoAssinaturasPendentes(idPrograma, assinantes, nomeArquivo)
                .doOnSuccess(idDocumento -> {
                    programaProcessamentoService
                            .marcarCriacaoArquivoProgramaEdocs(
                                    idPrograma,
                                    assinantes,
                                    idDocumento);
                })
                .doOnError(e -> {
                    logger.error("Erro ao integrar com E-Docs para criar arquivo em fase assinatura. Programa {}",
                            idPrograma, e);
                })
                .subscribe();
    }

    @Async
    public void assinarArquivoFaseAssinaturaEdocsServidor(Long idPrograma, String idDocumentoCapturadoEdocs,
            String subAssinante) {
        integracaoEdocsService.assinarArquivoFaseAssinaturaEdocsServidor(idPrograma, idDocumentoCapturadoEdocs)
                .doOnSuccess(idDocumento -> {
                    programaProcessamentoService
                            .marcarProgramaAssinado(
                                    idPrograma,
                                    subAssinante);
                })
                .doOnError(e -> {
                    logger.error("Erro ao integrar com E-Docs para assinar arquivo em fase assinatura. Programa {}",
                            idPrograma, e);
                })
                .subscribe();
    }

    @Async
    public void autuarProgramaEdocs(ProgramaDto programaDto) {
        integracaoEdocsService.autuarProgramaProjetoReativo(
            programaDto.id(),
			programaDto.idDocumentoCapturadoEdocs(), programaDto)
            .doOnSuccess( ctx -> {
                programaProcessamentoService
                        .marcarProgramaAutuadoEdocsEAvisoAutuado(programaDto.id(), ctx.getAssinantes(), ctx.getProtocolo() , ctx.getIdProcesso() ) ;
            })
            .doOnError(e -> {
                logger.error("Erro ao autuar um novo processo no E-Docs. Programa {}",
                programaDto.id(), e);
            })
            .subscribe();
    }

}
