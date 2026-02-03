package br.gov.es.siscap.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import br.gov.es.siscap.dto.ProjetoDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsyncExecutorService {

    private final RelatoriosService relatoriosService;
    private final ProjetoService service;
    private final IntegraccaoEdocsService integracaoEdocsService;

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
        integracaoEdocsService.reentranharDespacharDicProccessoComplementacaoSUBCAP(resource, nomeArquivo, idProjeto.longValue());
    }

    @Async
    public void despacharProcessoOrgaoOrigemEdocs(Long idProjeto, List<ProjetoCamposComplementacaoDto> complementos) {
        integracaoEdocsService.despacharProccessoEdocsOrgaoOrigem( idProjeto.longValue(), complementos );
    }

    @Async
    public void encerrarProcessoEdocs(ProjetoDto projetoDto) {
        integracaoEdocsService.encerrarProcessoEdocs(projetoDto);
    }

    @Async
    public void assinarCapturaParecerDIC( Long idProjeto, Long idParecer ) {
        integracaoEdocsService.assinarCapturaParecerDIC( idProjeto, idParecer );
    }

    @Async
    public void entranharPareceresDIC( Long idProjeto ) {
        integracaoEdocsService.entranharPareceresDIC( idProjeto );
    }

    @Async
    public void criarArquivoFaseAssinaturaEdocsServidor( Long idPrograma, List<String> assinantes, String nomeArquivo ) {
        integracaoEdocsService.enviarArquivoAssinaturasPendentes( idPrograma, assinantes, nomeArquivo );
    }

    // @Async
    // public void assinarArquivoFaseAssinaturaEdocsServidor( Long idPrograma, String idDocumentoCapturadoEdocs ) {
    //     integracaoEdocsService.assinarArquivoFaseAssinaturaEdocsServidor( idPrograma, idDocumentoCapturadoEdocs );
    // }

}
