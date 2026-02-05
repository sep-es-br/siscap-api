package br.gov.es.siscap.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.dto.acessocidadaoapi.EmailSubResponseDto;
import br.gov.es.siscap.enums.TipoStatusAssinaturaEnum;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaAssinaturaEdocs;
import br.gov.es.siscap.repository.ProgramaAssinaturaEdocsRepository;
import br.gov.es.siscap.repository.ProgramaRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgramaProcessamentoService {

    private final ProgramaAssinaturaEdocsService programaAssinaturaEdocsService;
    private final AcessoCidadaoService acessoCidadaoService;
    private final EmailService emailService;
    private final ProgramaRepository repository;
    private final ProgramaAssinaturaEdocsRepository programaAssinaturaEdocsRepository;

    private final Logger logger = LogManager.getLogger(ProgramaProcessamentoService.class);

    public void marcarCriacaoArquivoProgramaEdocs(Long idPrograma, List<String> assinantesEdocsPrograma,
            String idDocumentoEdocs) {
        this.marcarComoAguardandoAssinaturas(idPrograma, assinantesEdocsPrograma, idDocumentoEdocs);
        this.enviarAvisoSolicitarAssinaturaPrograma(idPrograma, assinantesEdocsPrograma);
    }

    @Transactional
    private void marcarComoAguardandoAssinaturas(Long idPrograma, List<String> assinantesEdocsPrograma,
            String idDocumentoEdocs) {
        logger.info("Registra as pendencias de assinatura no programa;");
        Programa programa = this.buscarPrograma(idPrograma);
        if (programaAssinaturaEdocsService.buscarPorPrograma(programa).isEmpty()) {
            programaAssinaturaEdocsService.cadastrar(programa, assinantesEdocsPrograma);
        }
        programa.setIdDocumentoCapturadoEdocs(idDocumentoEdocs);
        repository.save(programa);
    }

    private boolean enviarAvisoSolicitarAssinaturaPrograma(Long idPrograma, List<String> subAssinantes) {

        List<String> erros = new ArrayList<>();

        if (subAssinantes.isEmpty()) {
            erros.add("Erro ao enviar solicitação para assinatura do programa id " + idPrograma
                    + " assinaturas não informadas.");
            throw new ValidacaoSiscapException(erros);
        }

        List<String> emailsInteressadosList = new ArrayList<String>();

        subAssinantes.forEach(sub -> {
            EmailSubResponseDto emailsSub = acessoCidadaoService.buscarEmailsPorSub(sub);
            if (emailsSub.corporativo() != null && !emailsSub.corporativo().isBlank()) {
                emailsInteressadosList.add(emailsSub.corporativo());
            } else if (emailsSub.email() != null && !emailsSub.email().isBlank()) {
                emailsInteressadosList.add(emailsSub.email());
            }
        });

        Programa programa = this.buscarPrograma(idPrograma);

        String tituloPrograma = programa.getTitulo();
        String siglaPrograma = programa.getSigla();

        boolean confirmacaoEnvioEmail;

        try {

            EnvioEmailDicDetalhesDto envioEmailDetalhesDto = new EnvioEmailDicDetalhesDto(idPrograma,
                    "",
                    "",
                    emailsInteressadosList,
                    tituloPrograma,
                    siglaPrograma,"");

            confirmacaoEnvioEmail = emailService.enviarEmailSolicitandoAssinaturasPrograma(envioEmailDetalhesDto);

            if (confirmacaoEnvioEmail) {
                logger.info(
                        "Email aviso para solicitacao de assinaturas enviado com sucesso para o programa id "
                                + idPrograma);
            } else {
                erros.add("Erro ao enviar aviso para solicitacao de assinaturas do programa id " + idPrograma);
            }

        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        } catch (MessagingException e) {
            logger.error(e.getMessage());
        }

        if (!erros.isEmpty()) {
            erros.forEach(logger::error);
            throw new ValidacaoSiscapException(erros);
        }

        return true;

    }

    private Programa buscarPrograma(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Programa não encontrado"));
    }

    @Transactional
    public void marcarProgramaAssinado(Long idPrograma, String subAssinante) {

        Programa programa = repository.findById(idPrograma)
                .orElseThrow(() -> new ValidacaoSiscapException(List.of("Programa não encontrado.")));

        ProgramaAssinaturaEdocs assinatura = programa.getProgramaAssinantesEdocsSet()
                .stream()
                .filter(a -> subAssinante.equals(a.getPessoa().getSub()))
                .findFirst()
                .orElseThrow(() -> new ValidacaoSiscapException(
                        List.of("Existe(m) documento(s) a serem assinados.")));

        assinatura.setDataAssinatura(LocalDateTime.now());
        assinatura.setStatusAssinatura(TipoStatusAssinaturaEnum.ASSINADO.getValue());
        programaAssinaturaEdocsRepository.saveAndFlush(assinatura);

    }

    public void marcarProgramaAutuadoEdocsEAvisoAutuado(Long idPrograma, List<String> assinantesEdocsPrograma,
            String protocoloEdocs, String idProcessoEdocs) {
        marcarProgramaAutuado(idPrograma, protocoloEdocs, idProcessoEdocs);
        enviarAvisoProgramaAutuado(idPrograma, assinantesEdocsPrograma);
    }

    @Transactional
    public void marcarProgramaAutuado(Long idPrograma, String protocoloEdocs, String idProcessoEdocs) {

        Programa programa = repository.findById(idPrograma)
                .orElseThrow(() -> new ValidacaoSiscapException(List.of("Programa não encontrado.")));

        programa.setIdProcessoEdocs(idProcessoEdocs);
        programa.setProtocoloEdocs(protocoloEdocs);

        repository.saveAndFlush(programa);

    }

    private boolean enviarAvisoProgramaAutuado(Long idPrograma, List<String> subAssinantes) {

        List<String> erros = new ArrayList<>();

        if (subAssinantes.isEmpty()) {
            erros.add("Erro ao enviar solicitação para assinatura do programa id " + idPrograma
                    + " assinaturas não informadas.");
            throw new ValidacaoSiscapException(erros);
        }

        List<String> emailsInteressadosList = new ArrayList<String>();

        subAssinantes.forEach(sub -> {
            EmailSubResponseDto emailsSub = acessoCidadaoService.buscarEmailsPorSub(sub);
            if (emailsSub.corporativo() != null && !emailsSub.corporativo().isBlank()) {
                emailsInteressadosList.add(emailsSub.corporativo());
            } else if (emailsSub.email() != null && !emailsSub.email().isBlank()) {
                emailsInteressadosList.add(emailsSub.email());
            }
        });

        Programa programa = this.buscarPrograma(idPrograma);

        String tituloPrograma = programa.getTitulo();
        String siglaPrograma = programa.getSigla();
        String protocoloEdocsPrograma = programa.getProtocoloEdocs();

        boolean confirmacaoEnvioEmail;

        try {

            EnvioEmailDicDetalhesDto envioEmailDetalhesDto = new EnvioEmailDicDetalhesDto(idPrograma,
                    "",
                    "",
                    emailsInteressadosList,
                    tituloPrograma,
                    siglaPrograma,
                    protocoloEdocsPrograma);

            confirmacaoEnvioEmail = emailService.enviarEmailSolicitandoAssinaturasPrograma(envioEmailDetalhesDto);

            if (confirmacaoEnvioEmail) {
                logger.info(
                        "Email aviso para solicitacao de assinaturas enviado com sucesso para o programa id "
                                + idPrograma);
            } else {
                erros.add("Erro ao enviar aviso para solicitacao de assinaturas do programa id " + idPrograma);
            }

        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        } catch (MessagingException e) {
            logger.error(e.getMessage());
        }

        if (!erros.isEmpty()) {
            erros.forEach(logger::error);
            throw new ValidacaoSiscapException(erros);
        }

        return true;

    }

}
