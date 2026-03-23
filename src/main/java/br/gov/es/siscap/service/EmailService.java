package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.CartaConsultaDetalhesDto;
import br.gov.es.siscap.dto.EnvioEmailDetalhesDto;
import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import br.gov.es.siscap.dto.ProspeccaoDetalhesDto;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import br.gov.es.siscap.dto.opcoes.ObjetoOpcoesDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.utils.EnvioAnaliseGestorDicEmailBuilder;
import br.gov.es.siscap.utils.EnvioArquivamentoDicEmailBuilder;
import br.gov.es.siscap.utils.EnvioAvisoCapturaPareceresEmailBuilder;
import br.gov.es.siscap.utils.EnvioAvisoParecerGeocSubcapRealizadoEmailBuilder;
import br.gov.es.siscap.utils.EnvioAvisoPedidoAssinaturaProgramaEmailBuilder;
import br.gov.es.siscap.utils.EnvioAvisoPedidoParecerGerenciaSubcapEmailBuilder;
import br.gov.es.siscap.utils.EnvioAvisoProgramaAutuadoEdocsEmailBuilder;
import br.gov.es.siscap.utils.EnvioAvisoSubcapDicAutuadoEmailBuilder;
import br.gov.es.siscap.utils.EnvioComplementoDicEmailBuilder;
import br.gov.es.siscap.utils.EnvioDicElegivelEmailBuilder;
import br.gov.es.siscap.utils.EnvioPedidoParecerOrcamentarioEstrategicoEmailBuilder;
import br.gov.es.siscap.utils.EnvioRevisaoDicEmailBuilder;
import br.gov.es.siscap.utils.ProspeccaoEmailBuilder;
import br.gov.es.siscap.utils.email.sender.EmailSenderBase;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

	@Value("${email.remetente.endereco}")
	private String REMETENTE_ENDERECO;

	@Value("${email.remetente.apelido}")
	private String REMETENTE_APELIDO;

	@Value("${email.remetente.endereco-nao-responda}")
	private String REMETENTE_ENDERECO_NAO_RESPONDA;

	@Value("${email.destinatario-parecer.orcamentario}")
	private String DESTINO_PARECER_ORCAMENTARIO;

	@Value("${email.destinatario-parecer.estrategico}")
	private String DESTINO_PARECER_ESTRATEGICO;

	private final JavaMailSender sender;

	private final RelatoriosService relatoriosService;

	private final EmailSenderBase emailSender;

	private final PessoaService pessoaPessoaService;

	private final AcessoCidadaoService acessoCidadaoService;

	private final EnvioRevisaoDicEmailBuilder builderEnvioEmailRevisao;
	private final EnvioAnaliseGestorDicEmailBuilder builderEnvioEmailAnaliseGestorDic;
	private final EnvioPedidoParecerOrcamentarioEstrategicoEmailBuilder builderEnvioEmailPedidoParecerSUBEPPSUBEO;
	private final EnvioArquivamentoDicEmailBuilder builderEnvioEmailArquivamentoDIC;
	private final EnvioComplementoDicEmailBuilder builderEnvioEmailComplementoDIC;
	private final EnvioAvisoCapturaPareceresEmailBuilder builderEnvioEmailAvisoCapturaParecer;
	private final EnvioAvisoSubcapDicAutuadoEmailBuilder builderEnvioEmailAvisoDicAutuado;
	private final EnvioAvisoPedidoParecerGerenciaSubcapEmailBuilder builderEnvioAvisoPedidoParecerGEOC;
	private final EnvioAvisoParecerGeocSubcapRealizadoEmailBuilder builderEnvioEmailAvisoParecerGEOCRealizado;
	private final EnvioAvisoPedidoAssinaturaProgramaEmailBuilder envioAvisoPedidoAssinaturaProgramaEmailBuilder;
	private final EnvioAvisoProgramaAutuadoEdocsEmailBuilder envioAvisoProgramaAutuadoEmailBuilder;
	private final EnvioDicElegivelEmailBuilder envioDicElegivelEmailBuilder;

	public boolean enviarEmail(ProspeccaoDetalhesDto prospeccaoDetalhesDto, List<String> emailsInteressadosList,
			String nomeArquivo) throws MessagingException, UnsupportedEncodingException {

		List<Boolean> confirmacaoEnvioEmailList = new ArrayList<>();

		MimeMessage mensagem = this.sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

		String assuntoEmail = ProspeccaoEmailBuilder.montarAssuntoEmail(prospeccaoDetalhesDto);
		String corpoEmail = ProspeccaoEmailBuilder.montarCorpoEmail(prospeccaoDetalhesDto);

		String remetente = Objects.requireNonNull(REMETENTE_ENDERECO, "Remetente não pode ser null");
		String apelido = Objects.requireNonNull(REMETENTE_APELIDO, "Remetente apelido não pode ser null");

		helper.setFrom(remetente, apelido);
		helper.setSubject(assuntoEmail != null ? assuntoEmail : "Assunto nao definido");
		helper.setText(corpoEmail != null ? corpoEmail : "Corpo do email nao definido", true);

		this.anexarRelatorios(helper, prospeccaoDetalhesDto.cartaConsultaDetalhes(), nomeArquivo);

		for (String emailInteressado : emailsInteressadosList) {
			helper.setTo(emailInteressado != null ? emailInteressado : "");
			try {
				this.sender.send(helper.getMimeMessage());
				confirmacaoEnvioEmailList.add(true);
			} catch (MailException e) {
				confirmacaoEnvioEmailList.add(false);
				throw new ValidacaoSiscapException(List.of(e.getMessage()));
			}
		}

		return confirmacaoEnvioEmailList.stream().allMatch(Boolean::booleanValue);
	}

	private void anexarRelatorios(MimeMessageHelper helper, CartaConsultaDetalhesDto cartaConsultaDetalhesDto,
			String nomeArquivo) throws MessagingException {

		ObjetoOpcoesDto cartaConsultaObjeto = cartaConsultaDetalhesDto.objeto();

		if (cartaConsultaObjeto.tipo().equals("Projeto")) {
			this.prepararRecursoRelatorio(helper, cartaConsultaObjeto.id().intValue(), nomeArquivo);
		}

		List<OpcoesDto> projetosPropostosList = cartaConsultaDetalhesDto.projetosPropostos();

		if (!projetosPropostosList.isEmpty()) {
			for (OpcoesDto projetoProposto : projetosPropostosList) {
				this.prepararRecursoRelatorio(helper, projetoProposto.id().intValue(), nomeArquivo);
			}
		}
	}

	private void prepararRecursoRelatorio(MimeMessageHelper helper, int idProjeto, String nomeArquivo)
			throws MessagingException {
		Resource relatorioDIC = this.relatoriosService.gerarArquivo("DIC", idProjeto);
		helper.addAttachment(nomeArquivo, relatorioDIC);
	}

	public boolean enviarEmailAnaliseDIC(EnvioEmailDetalhesDto envioEmailDicDetalhesDto)
			throws MessagingException, UnsupportedEncodingException {

		builderEnvioEmailAnaliseGestorDic.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);

		return emailSender.enviarEmail(builderEnvioEmailAnaliseGestorDic,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailPareceresEstrategicoOrcamentario(EnvioEmailDetalhesDto envioEmailDicDetalhesDto)
			throws MessagingException, UnsupportedEncodingException {

		List<String> emailsDestinatarios = new ArrayList<>();

		emailsDestinatarios.add(this.DESTINO_PARECER_ESTRATEGICO);
		emailsDestinatarios.add(this.DESTINO_PARECER_ORCAMENTARIO);

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto2 = new EnvioEmailDetalhesDto(
				envioEmailDicDetalhesDto.idProjeto(),
				envioEmailDicDetalhesDto.nomeResponsavelEnvioEmail(),
				null,
				null,
				envioEmailDicDetalhesDto.nomeGestor(),
				emailsDestinatarios,
				envioEmailDicDetalhesDto.tituloProjeto(),
				null,
				null,
				null,
				null, null, null, "", "", "", null);

		builderEnvioEmailPedidoParecerSUBEPPSUBEO.setDtoMontagemEmailDic(envioEmailDicDetalhesDto2);
		builderEnvioEmailPedidoParecerSUBEPPSUBEO.setSiglaProjeto(envioEmailDicDetalhesDto.tituloProjeto());

		return emailSender.enviarEmail(builderEnvioEmailPedidoParecerSUBEPPSUBEO,
				envioEmailDicDetalhesDto2.emailsInteressadosList());

	}

	public boolean enviarEmailRevisarProjeto(List<String> emailsInteressadosList, String justificativa,
			String nomeResponsavelEnvio, Projeto projeto, String responsavelProponenteProjeto)
			throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDetalhesDto(
				projeto.getId(),
				nomeResponsavelEnvio,
				null,
				null,
				responsavelProponenteProjeto,
				emailsInteressadosList,
				projeto.getSigla(),
				null,
				null,
				null,
				justificativa, null, null, "", "", "", null);

		builderEnvioEmailRevisao.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioEmailRevisao.setSiglaProjeto(projeto.getSigla());

		return emailSender.enviarEmail(builderEnvioEmailRevisao, envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailArquivamentorProjeto(List<String> emailsInteressadosList, String justificativa,
			String nomeResponsavelEnvio,
			String descricaoProjeto,
			String codigoMotivoArquivamento,
			String descricaoTipoMotivoArquivamento,
			String responsavelProponenteProjeto,
			Long idProjeto) throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDetalhesDto(
				idProjeto,
				nomeResponsavelEnvio,
				null,
				null,
				responsavelProponenteProjeto,
				emailsInteressadosList,
				descricaoProjeto,
				codigoMotivoArquivamento,
				descricaoTipoMotivoArquivamento,
				justificativa, null, null, null, "", "", "", null);

		builderEnvioEmailArquivamentoDIC.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioEmailArquivamentoDIC.setSiglaProjeto(descricaoProjeto);

		return emailSender.enviarEmail(builderEnvioEmailArquivamentoDIC,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailComplemetacaoProjeto(List<String> emailsInteressadosList,
			String nomeResponsavelEnvio,
			String responsavelProponenteProjeto,
			String descricaoProjeto,
			List<ProjetoCamposComplementacaoDto> camposComplementar,
			Long idProjeto)
			throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDetalhesDto(
				idProjeto,
				nomeResponsavelEnvio,
				null,
				null,
				responsavelProponenteProjeto,
				emailsInteressadosList,
				descricaoProjeto,
				null,
				null,
				null, null, camposComplementar, null, "", "", "", null);

		builderEnvioEmailComplementoDIC.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioEmailComplementoDIC.setSiglaProjeto(descricaoProjeto);

		return emailSender.enviarEmail(builderEnvioEmailComplementoDIC,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailPareceresCapturadosProjeto(List<String> emailsInteressadosList, Long idProjeto,
			String siglaProjeto)
			throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDetalhesDto(
				idProjeto,
				null,
				null,
				null,
				null,
				emailsInteressadosList,
				null,
				null,
				null,
				null,
				null, null, null, "", "", "", null);

		builderEnvioEmailAvisoCapturaParecer.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioEmailAvisoCapturaParecer.setSiglaProjeto(siglaProjeto);

		return emailSender.enviarEmail(builderEnvioEmailAvisoCapturaParecer,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailAvisoSubcapDicAutuado(List<String> emailsInteressadosList, String descricaoDic,
			Long idProjeto) throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDetalhesDto(idProjeto,
				null,
				null,
				null,
				null,
				emailsInteressadosList,
				null,
				null,
				null,
				null, null, null, null, "", "", "", null);

		builderEnvioEmailAvisoDicAutuado.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioEmailAvisoDicAutuado.setSiglaProjeto(descricaoDic);

		return emailSender.enviarEmail(builderEnvioEmailAvisoDicAutuado,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailAvisoParecerGerenciaSubcap(List<String> emailsInteressadosList, String descricaoDic,
			Long idProjeto) throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDetalhesDto(idProjeto,
				null,
				null,
				null,
				null,
				emailsInteressadosList,
				null,
				null,
				null,
				null, null, null, null, "", "", "", null);

		builderEnvioAvisoPedidoParecerGEOC.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioAvisoPedidoParecerGEOC.setSiglaProjeto(descricaoDic);

		return emailSender.enviarEmail(builderEnvioAvisoPedidoParecerGEOC,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailAvisoParecerGeocSubcapRealizado(List<String> emailsInteressadosList, String descricaoDic,
			Long idProjeto) throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDetalhesDto(idProjeto,
				null,
				null,
				null,
				null,
				emailsInteressadosList,
				null,
				null,
				null,
				null, null, null, null, "", "", "", null);

		builderEnvioEmailAvisoParecerGEOCRealizado.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioEmailAvisoParecerGEOCRealizado.setSiglaProjeto(descricaoDic);

		return emailSender.enviarEmail(builderEnvioEmailAvisoParecerGEOCRealizado,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailSolicitandoAssinaturasPrograma(EnvioEmailDetalhesDto envioEmailDetalhesProgramaDto)
			throws MessagingException, UnsupportedEncodingException {

		envioAvisoPedidoAssinaturaProgramaEmailBuilder
				.setSubEmailDestinatarios(envioEmailDetalhesProgramaDto.subAssinantesEmails());
		envioAvisoPedidoAssinaturaProgramaEmailBuilder.setDtoMontagemEmailDic(envioEmailDetalhesProgramaDto);

		boolean todosEnviados = true;

		for (String email : envioEmailDetalhesProgramaDto.emailsInteressadosList()) {

			String nomeDestinatario = Optional
					.ofNullable(envioEmailDetalhesProgramaDto.subAssinantesEmails().get(email))
					.filter(sub -> !sub.isBlank())
					.flatMap(sub -> Optional.ofNullable(pessoaPessoaService.buscarPorSub(sub))
							.map(Pessoa::getNome)
							.filter(nome -> !nome.isBlank())
							.or(() -> buscarNomeNoAcessoCidadaoOptional(sub)))
					.orElse("");

			envioAvisoPedidoAssinaturaProgramaEmailBuilder.setEmailEmProcessamento(email);
			envioAvisoPedidoAssinaturaProgramaEmailBuilder.setNomeDestinatario(nomeDestinatario);

			boolean enviado = emailSender.enviarEmail(
					envioAvisoPedidoAssinaturaProgramaEmailBuilder,
					List.of(email));

			if (!enviado) {
				todosEnviados = false;
			}

		}

		return todosEnviados;

	}

	private Optional<String> buscarNomeNoAcessoCidadaoOptional(String sub) {
		return Optional.ofNullable(acessoCidadaoService.buscarPessoaPorSub(sub))
				.map(AgentePublicoACDto::nome)
				.filter(nome -> !nome.isBlank());
	}

	public boolean enviarEmailAvisoProgramaAutuado(EnvioEmailDetalhesDto envioEmailProgramaAutuadoDetalhesDto)
			throws MessagingException, UnsupportedEncodingException {

		envioAvisoProgramaAutuadoEmailBuilder
				.setSubEmailDestinatarios(envioEmailProgramaAutuadoDetalhesDto.subAssinantesEmails());
		envioAvisoProgramaAutuadoEmailBuilder.setDtoMontagemEmailDic(envioEmailProgramaAutuadoDetalhesDto);

		boolean todosEnviados = true;

		for (String email : envioEmailProgramaAutuadoDetalhesDto.emailsInteressadosList()) {

			String nomeDestinatario = Optional
					.ofNullable(envioEmailProgramaAutuadoDetalhesDto.subAssinantesEmails().get(email))
					.filter(sub -> !sub.isBlank())
					.flatMap(sub -> Optional.ofNullable(pessoaPessoaService.buscarPorSub(sub))
							.map(Pessoa::getNome)
							.filter(nome -> !nome.isBlank())
							.or(() -> buscarNomeNoAcessoCidadaoOptional(sub)))
					.orElse("");

			envioAvisoProgramaAutuadoEmailBuilder.setEmailEmProcessamento(email);
			envioAvisoProgramaAutuadoEmailBuilder.setNomeDestinatario(nomeDestinatario);

			boolean enviado = emailSender.enviarEmail(
					envioAvisoProgramaAutuadoEmailBuilder,
					List.of(email));
			if (!enviado) {
				todosEnviados = false;
			}
		}

		return todosEnviados;

	}

	public boolean enviarEmailAvisoDicElegivel(List<String> emailsInteressadosList, String descricaoDic,
			Long idProjeto) throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDetalhesDto(idProjeto,
				null,
				null,
				null,
				null,
				emailsInteressadosList,
				null,
				null,
				null,
				null, null, null, null, "", "", "", null);

		builderEnvioEmailAvisoParecerGEOCRealizado.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioEmailAvisoParecerGEOCRealizado.setSiglaProjeto(descricaoDic);

		return emailSender.enviarEmail(builderEnvioEmailAvisoParecerGEOCRealizado,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

}
