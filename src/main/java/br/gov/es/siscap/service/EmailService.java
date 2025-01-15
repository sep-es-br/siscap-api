package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProspeccaoDetalhesDto;
import br.gov.es.siscap.utils.ProspeccaoEmailBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSenderImpl sender;

	public void enviarEmail(ProspeccaoDetalhesDto prospeccaoDetalhesDto, List<String> emailsInteressadosList) throws MessagingException {

		MimeMessage mensagem = this.sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

		String assuntoEmail = ProspeccaoEmailBuilder.montarAssuntoEmail(prospeccaoDetalhesDto);
		String corpoEmail = ProspeccaoEmailBuilder.montarCorpoEmail(prospeccaoDetalhesDto);

		helper.setSubject(assuntoEmail);
		helper.setText(corpoEmail, true);

		for (String emailInteressado : emailsInteressadosList) {
			helper.setTo(emailInteressado);
			try {
				this.sender.send(helper.getMimeMessage());
			} catch (MailException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
