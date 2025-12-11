package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.CartaConsultaDetalhesDto;
import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import br.gov.es.siscap.dto.ProspeccaoDetalhesDto;
import br.gov.es.siscap.dto.opcoes.ObjetoOpcoesDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.utils.EnvioAnaliseGestorDicEmailBuilder;
import br.gov.es.siscap.utils.EnvioArquivamentoDicEmailBuilder;
import br.gov.es.siscap.utils.EnvioAvisoCapturaPareceresEmailBuilder;
import br.gov.es.siscap.utils.EnvioAvisoParecerGeocSubcapRealizadoEmailBuilder;
import br.gov.es.siscap.utils.EnvioAvisoPedidoParecerGerenciaSubcapEmailBuilder;
import br.gov.es.siscap.utils.EnvioAvisoSubcapDicAutuadoEmailBuilder;
import br.gov.es.siscap.utils.EnvioComplementoDicEmailBuilder;
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

	private final EnvioRevisaoDicEmailBuilder builderEnvioEmailRevisao;
	private final EnvioAnaliseGestorDicEmailBuilder builderEnvioEmailAnaliseGestorDic;
	private final EnvioPedidoParecerOrcamentarioEstrategicoEmailBuilder builderEnvioEmailPedidoParecerSUBEPPSUBEO;
	private final EnvioArquivamentoDicEmailBuilder builderEnvioEmailArquivamentoDIC;
	private final EnvioComplementoDicEmailBuilder builderEnvioEmailComplementoDIC;
	private final EnvioAvisoCapturaPareceresEmailBuilder builderEnvioEmailAvisoCapturaParecer;
	private final EnvioAvisoSubcapDicAutuadoEmailBuilder builderEnvioEmailAvisoDicAutuado;
	private final EnvioAvisoPedidoParecerGerenciaSubcapEmailBuilder builderEnvioAvisoPedidoParecerGEOC;
	private final EnvioAvisoParecerGeocSubcapRealizadoEmailBuilder builderEnvioEmailAvisoParecerGEOCRealizado;

	public boolean enviarEmail(ProspeccaoDetalhesDto prospeccaoDetalhesDto, List<String> emailsInteressadosList,
			String nomeArquivo) throws MessagingException, UnsupportedEncodingException {

		List<Boolean> confirmacaoEnvioEmailList = new ArrayList<>();

		MimeMessage mensagem = this.sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

		String assuntoEmail = ProspeccaoEmailBuilder.montarAssuntoEmail(prospeccaoDetalhesDto);
		String corpoEmail = ProspeccaoEmailBuilder.montarCorpoEmail(prospeccaoDetalhesDto);

		helper.setFrom(REMETENTE_ENDERECO, REMETENTE_APELIDO);
		helper.setSubject(assuntoEmail);
		helper.setText(corpoEmail, true);

		this.anexarRelatorios(helper, prospeccaoDetalhesDto.cartaConsultaDetalhes(), nomeArquivo);

		for (String emailInteressado : emailsInteressadosList) {
			helper.setTo(emailInteressado);
			try {
				this.sender.send(helper.getMimeMessage());
				confirmacaoEnvioEmailList.add(true);
			} catch (MailException e) {
				confirmacaoEnvioEmailList.add(false);
				throw new RuntimeException(e);
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

	public boolean enviarEmailAnaliseDIC(EnvioEmailDicDetalhesDto envioEmailDicDetalhesDto)
			throws MessagingException, UnsupportedEncodingException {

		builderEnvioEmailAnaliseGestorDic.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);

		return emailSender.enviarEmail(builderEnvioEmailAnaliseGestorDic,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailPareceresEstrategicoOrcamentario(EnvioEmailDicDetalhesDto envioEmailDicDetalhesDto)
			throws MessagingException, UnsupportedEncodingException {

		List<String> emailsDestinatarios = new ArrayList<>();

		emailsDestinatarios.add(this.DESTINO_PARECER_ESTRATEGICO);
		emailsDestinatarios.add(this.DESTINO_PARECER_ORCAMENTARIO);

		EnvioEmailDicDetalhesDto envioEmailDicDetalhesDto2 = new EnvioEmailDicDetalhesDto(
				envioEmailDicDetalhesDto.idProjeto(),
				envioEmailDicDetalhesDto.nomeResponsavelEnvioEmail(),
				"",
				"",
				envioEmailDicDetalhesDto.nomeGestor(),
				emailsDestinatarios,
				envioEmailDicDetalhesDto.tituloProjeto(),
				"",
				"",
				"",
				"");

		builderEnvioEmailPedidoParecerSUBEPPSUBEO.setDtoMontagemEmailDic(envioEmailDicDetalhesDto2);
		builderEnvioEmailPedidoParecerSUBEPPSUBEO.setSiglaProjeto(envioEmailDicDetalhesDto.tituloProjeto());

		return emailSender.enviarEmail(builderEnvioEmailPedidoParecerSUBEPPSUBEO,
				envioEmailDicDetalhesDto2.emailsInteressadosList());

	}

	public boolean enviarEmailRevisarProjeto(List<String> emailsInteressadosList, String justificativa,
			String nomeResponsavelEnvio, Projeto projeto, String responsavelProponenteProjeto)
			throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDicDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDicDetalhesDto(
				projeto.getId(),
				nomeResponsavelEnvio,
				"",
				"",
				responsavelProponenteProjeto,
				emailsInteressadosList,
				projeto.getSigla(),
				"",
				"",
				"",
				justificativa);

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

		EnvioEmailDicDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDicDetalhesDto(
				idProjeto,
				nomeResponsavelEnvio,
				"",
				"",
				responsavelProponenteProjeto,
				emailsInteressadosList,
				descricaoProjeto,
				codigoMotivoArquivamento,
				descricaoTipoMotivoArquivamento,
				justificativa, "");

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

		EnvioEmailDicDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDicDetalhesDto(
				idProjeto,
				nomeResponsavelEnvio,
				"",
				"",
				responsavelProponenteProjeto,
				emailsInteressadosList,
				descricaoProjeto,
				"",
				"",
				"", "");

		builderEnvioEmailComplementoDIC.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioEmailComplementoDIC.setSiglaProjeto(descricaoProjeto);

		return emailSender.enviarEmail(builderEnvioEmailComplementoDIC,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailPareceresCapturadosProjeto(List<String> emailsInteressadosList, Long idProjeto)
			throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDicDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDicDetalhesDto(
				idProjeto,
				"",
				"",
				"",
				"",
				emailsInteressadosList,
				"", // projeto.getSigla(),
				"",
				"",
				"",
				"");

		builderEnvioEmailAvisoCapturaParecer.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);

		return emailSender.enviarEmail(builderEnvioEmailAvisoCapturaParecer,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailAvisoSubcapDicAutuado(List<String> emailsInteressadosList, String descricaoDic,
			Long idProjeto) throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDicDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDicDetalhesDto(idProjeto,
				"",
				"",
				"",
				"",
				emailsInteressadosList,
				"",
				"",
				"",
				"", "");

		builderEnvioEmailAvisoDicAutuado.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioEmailAvisoDicAutuado.setSiglaProjeto(descricaoDic);

		return emailSender.enviarEmail(builderEnvioEmailAvisoDicAutuado,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailAvisoParecerGerenciaSubcap(List<String> emailsInteressadosList, String descricaoDic,
			Long idProjeto) throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDicDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDicDetalhesDto(idProjeto,
				"",
				"",
				"",
				"",
				emailsInteressadosList,
				"",
				"",
				"",
				"", "");

		builderEnvioAvisoPedidoParecerGEOC.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioAvisoPedidoParecerGEOC.setSiglaProjeto(descricaoDic);

		return emailSender.enviarEmail(builderEnvioAvisoPedidoParecerGEOC,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

	public boolean enviarEmailAvisoParecerGeocSubcapRealizado(List<String> emailsInteressadosList, String descricaoDic,
			Long idProjeto) throws MessagingException, UnsupportedEncodingException {

		EnvioEmailDicDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDicDetalhesDto(idProjeto,
				"",
				"",
				"",
				"",
				emailsInteressadosList,
				"",
				"",
				"",
				"", "");

		builderEnvioEmailAvisoParecerGEOCRealizado.setDtoMontagemEmailDic(envioEmailDicDetalhesDto);
		builderEnvioEmailAvisoParecerGEOCRealizado.setSiglaProjeto(descricaoDic);

		return emailSender.enviarEmail(builderEnvioEmailAvisoParecerGEOCRealizado,
				envioEmailDicDetalhesDto.emailsInteressadosList());

	}

}
