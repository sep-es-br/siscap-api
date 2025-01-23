package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProspeccaoDetalhesDto;
import br.gov.es.siscap.utils.ProspeccaoEmailBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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

	private final JavaMailSenderImpl sender;

	public boolean enviarEmail(ProspeccaoDetalhesDto prospeccaoDetalhesDto, List<String> emailsInteressadosList) throws MessagingException, UnsupportedEncodingException {

		List<Boolean> confirmacaoEnvioEmailList = new ArrayList<>();

		MimeMessage mensagem = this.sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

		String assuntoEmail = ProspeccaoEmailBuilder.montarAssuntoEmail(prospeccaoDetalhesDto);
		String corpoEmail = ProspeccaoEmailBuilder.montarCorpoEmail(prospeccaoDetalhesDto);

		helper.setFrom(REMETENTE_ENDERECO, REMETENTE_APELIDO);
		helper.setSubject(assuntoEmail);
		helper.setText(corpoEmail, true);

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
}
