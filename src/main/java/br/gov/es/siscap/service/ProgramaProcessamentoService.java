package br.gov.es.siscap.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.siscap.dto.EnvioEmailDetalhesDto;
import br.gov.es.siscap.dto.ProgramaAssinaturaEdocsDto;
import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.acessocidadaoapi.EmailSubResponseDto;
import br.gov.es.siscap.enums.StatusProgramaEnum;
import br.gov.es.siscap.enums.TipoStatusAssinaturaEnum;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaAssinaturaEdocs;
import br.gov.es.siscap.models.ProgramaPessoa;
import br.gov.es.siscap.models.ProjetoPessoa;
import br.gov.es.siscap.repository.ProgramaAssinaturaEdocsRepository;
import br.gov.es.siscap.repository.ProgramaRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgramaProcessamentoService {

    @Value("${email.destinatario-subcap}")
	private String emailSubcap;

    private final ProgramaAssinaturaEdocsService programaAssinaturaEdocsService;
    private final AcessoCidadaoService acessoCidadaoService;
    private final EmailService emailService;
    private final ProgramaRepository repository;
    private final ProgramaAssinaturaEdocsRepository programaAssinaturaEdocsRepository;
    private final PessoaService pessoaService;

    private final Logger logger = LogManager.getLogger(ProgramaProcessamentoService.class);

    @Transactional
    public void marcarCriacaoArquivoProgramaEdocs(Long idPrograma, List<String> assinantesEdocsPrograma,
            String idDocumentoEdocs) {
        this.marcarComoAguardandoAssinaturas(idPrograma, assinantesEdocsPrograma, idDocumentoEdocs);
        this.enviarAvisoSolicitarAssinaturaPrograma(idPrograma, assinantesEdocsPrograma);
    }

    @Transactional
    public void marcarComoAguardandoAssinaturas(Long idPrograma, List<String> assinantesEdocsPrograma,
            String idDocumentoEdocs) {
        logger.info("Registra as pendencias de assinatura no programa;");
        Programa programa = this.buscarPrograma(idPrograma);
        if (programaAssinaturaEdocsService.buscarPorPrograma(programa).isEmpty()) {
            programaAssinaturaEdocsService.cadastrar(programa, assinantesEdocsPrograma);
        }
        programa.setIdDocumentoCapturadoEdocs(idDocumentoEdocs);
        programa.setStatus(StatusProgramaEnum.AGUARDANDOASSINATURAS.getValue());
        repository.save(programa);
    }

    public boolean enviarAvisoSolicitarAssinaturaPrograma(Long idPrograma, List<String> subAssinantes) {

        List<String> erros = new ArrayList<>();

        if (subAssinantes.isEmpty()) {
            erros.add("Erro ao enviar solicitação para assinatura do programa id " + idPrograma
                    + " assinaturas não informadas.");
            throw new ValidacaoSiscapException(erros);
        }

        List<String> emailsInteressadosList = new ArrayList<>();
        Map<String, String> emailsSubAssinates = new HashMap<>();

        subAssinantes.forEach(sub -> {
            EmailSubResponseDto emailsSub = acessoCidadaoService.buscarEmailsPorSub(sub);
            String emailAssinanteAC = "";

            if (emailsSub.corporativo() != null && !emailsSub.corporativo().isBlank()) {
                emailAssinanteAC = emailsSub.corporativo();
            } else if (emailsSub.email() != null && !emailsSub.email().isBlank()) {
                emailAssinanteAC = emailsSub.email();
            }
            emailsInteressadosList.add(emailAssinanteAC);
            emailsSubAssinates.put(emailAssinanteAC, sub);
        });

        Programa programa = this.buscarPrograma(idPrograma);

        String tituloPrograma = programa.getTitulo();
        String siglaPrograma = programa.getSigla();

        boolean confirmacaoEnvioEmail;

        EnvioEmailDetalhesDto envioEmailDetalhesDto = new EnvioEmailDetalhesDto(idPrograma,
                emailsInteressadosList,
                tituloPrograma,
                siglaPrograma,
                emailsSubAssinates);

        confirmacaoEnvioEmail = emailService.enviarEmailSolicitandoAssinaturasPrograma(envioEmailDetalhesDto);

        if (confirmacaoEnvioEmail) {
            logger.info(
                    "Email aviso para solicitacao de assinaturas enviado com sucesso para o programa id {}",
                    idPrograma);
        } else {
            erros.add("Erro ao enviar aviso para solicitacao de assinaturas do programa id " + idPrograma);
        }

        if (!erros.isEmpty()) {
            erros.forEach(logger::error);
            throw new ValidacaoSiscapException(erros);
        }

        return true;

    }

    private Programa buscarPrograma(long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Programa não encontrado"));
    }

    @Transactional
    public void marcarProgramaAssinado(long idPrograma, String subAssinante) {

        Programa programa = repository.findById(idPrograma)
                .orElseThrow(() -> new ValidacaoSiscapException(List.of("Programa não encontrado.")));

        ProgramaAssinaturaEdocs assinatura = programa.getProgramaAssinantesEdocsSet()
                .stream()
                .filter(a -> subAssinante.equals(a.getPessoa().getSub()))
                .findFirst()
                .orElseThrow(() -> new ValidacaoSiscapException(
                        List.of("Não existe(m) documento(s) a serem assinados.")));

        assinatura.setDataAssinatura(LocalDateTime.now());
        assinatura.setStatusAssinatura(TipoStatusAssinaturaEnum.ASSINADO.getValue());

        boolean todosJaAssinaram = programa.getProgramaAssinantesEdocsSet().stream().allMatch(
                assinante -> assinante.getStatusAssinatura().equals(TipoStatusAssinaturaEnum.ASSINADO.getValue()));

        if (todosJaAssinaram){
            programa.setStatus(StatusProgramaEnum.ASSINADO.getValue());
            emailService.enviarEmailAvisoProgramaAssinadoSubcap( List.of(emailSubcap), programa);
        }

        repository.saveAndFlush(programa);

        programaAssinaturaEdocsRepository.saveAndFlush(assinatura);

    }

    @Transactional
    public void marcarProgramaAutuadoEdocsEAvisoAutuado(ProgramaDto programaDto,
            String protocoloEdocs, String idProcessoEdocs) {

        Objects.requireNonNull(programaDto, "programaDto não pode ser nulo");

        marcarProgramaAutuado(programaDto.id(), protocoloEdocs, idProcessoEdocs);

        try {

            List<Long> idsPessoas = Optional
                    .ofNullable(programaDto.programaAssinantesEdocsDto())
                    .orElse(List.of())
                    .stream()
                    .map(ProgramaAssinaturaEdocsDto::idPessoa)
                    .toList();

            if (idsPessoas.isEmpty()) {
                logger.warn("Programa {} autuado sem assinantes para notificação",
                        programaDto.id());
                return;
            }

            List<String> subsAssinatesList = pessoaService.buscarSubsPorIds(idsPessoas);

            if (subsAssinatesList.isEmpty()) {
                logger.warn("Nenhum sub encontrado para notificação do programa {}",
                        programaDto.id());
                return;
            }

            enviarAvisoProgramaAutuado(programaDto.id(), subsAssinatesList);

        } catch (Exception e) {
            logger.error("Erro ao enviar aviso de autuação do programa {}",
                    programaDto.id(), e);
        }

    }

    public void marcarProgramaAutuado(long idPrograma, String protocoloEdocs, String idProcessoEdocs) {

        Programa programa = repository.findById(idPrograma)
                .orElseThrow(() -> new ValidacaoSiscapException(List.of("Programa não encontrado.")));

        programa.setIdProcessoEdocs(idProcessoEdocs);
        programa.setProtocoloEdocs(protocoloEdocs);
        programa.setStatus(StatusProgramaEnum.AUTUADO.getValue());

        repository.saveAndFlush(programa);

    }

    private boolean enviarAvisoProgramaAutuado(Long idPrograma, List<String> subAssinantes) {

        List<String> erros = new ArrayList<>();

        if (subAssinantes.isEmpty()) {
            erros.add("Erro ao enviar email de aviso sobre autuação do programa id " + idPrograma
                    + ", assinantes não informados.");
            throw new ValidacaoSiscapException(erros);
        }

        Map<String, String> emailsSubAssinates = acessoCidadaoService.buscarEmailsPorListaSub(subAssinantes);

        Programa programa = this.buscarPrograma(idPrograma);

        String tituloPrograma = programa.getTitulo();
        String siglaPrograma = programa.getSigla();
        String protocoloEdocsPrograma = programa.getProtocoloEdocs();

        List<String> subsEquipeCapacitacaoPrograma = programa.getProgramaPessoaSet()
                .stream()
                .map(ProgramaPessoa::getPessoa)
                .map(Pessoa::getSub)
                .toList();

        if (!subsEquipeCapacitacaoPrograma.isEmpty()) {
            emailsSubAssinates.putAll(acessoCidadaoService.buscarEmailsPorListaSub(subsEquipeCapacitacaoPrograma));
        }

        List<String> emailsInteressadosList = emailsSubAssinates.values()
                .stream()
                .distinct()
                .toList();

        boolean confirmacaoEnvioEmail;

        try {

            EnvioEmailDetalhesDto envioEmailDetalhesDto = new EnvioEmailDetalhesDto(idPrograma,
                    emailsInteressadosList,
                    tituloPrograma,
                    siglaPrograma,
                    emailsSubAssinates,
                    protocoloEdocsPrograma);

            confirmacaoEnvioEmail = emailService.enviarEmailAvisoProgramaAutuado(envioEmailDetalhesDto);

            if (confirmacaoEnvioEmail) {
                logger.info(
                        "E-mail de aviso de autuação enviado com sucesso. ProgramaId={}, ProtocoloEdocs={}",
                        idPrograma, protocoloEdocsPrograma);
            } else {
                erros.add(
                        "Falha ao enviar e-mail de aviso de autuação do programa no E-Docs. ProgramaId= " + idPrograma);
            }

        } catch (UnsupportedEncodingException | MessagingException e) {
            logger.error(e.getMessage());
        }

        if (!erros.isEmpty()) {
            erros.forEach(logger::error);
            throw new ValidacaoSiscapException(erros);
        }

        return true;

    }

    @Transactional
    public void atualizarIdDocumentoEdocsNoPrograma(long idPrograma, String idDocumentoAutuadoEdocs) {

        Programa programa = repository.findById(idPrograma)
                .orElseThrow(() -> new ValidacaoSiscapException(List.of("Programa não encontrado.")));

        programa.setIdDocumentoCapturadoEdocs(idDocumentoAutuadoEdocs);

        repository.saveAndFlush(programa);

    }

    @Transactional(readOnly = false)
    public void assinanteRecusouAssinarPrograma(long idPrograma, String subAssinante) {

        Programa programa = repository.findById(idPrograma)
                .orElseThrow(() -> new ValidacaoSiscapException(List.of("Programa não encontrado.")));

        ProgramaAssinaturaEdocs assinatura = programa.getProgramaAssinantesEdocsSet()
                .stream()
                .filter(a -> subAssinante.equals(a.getPessoa().getSub()))
                .findFirst()
                .orElseThrow(() -> new ValidacaoSiscapException(
                        List.of("Existe(m) documento(s) a serem assinados.")));

        assinatura.setDataAssinatura(null);
        assinatura.setDataRecusa(LocalDateTime.now());
        assinatura.setStatusAssinatura(TipoStatusAssinaturaEnum.RECUSOUSEASSINAR.getValue());
        assinatura.setJustificativaRecusa(this.programaAssinaturaEdocsService.resolverMensagemRecusaAssinanteGestor(subAssinante));

        programa.setStatus(StatusProgramaEnum.RECUSADO.getValue());

        repository.saveAndFlush(programa);

        programaAssinaturaEdocsRepository.saveAndFlush(assinatura);

    }

}
