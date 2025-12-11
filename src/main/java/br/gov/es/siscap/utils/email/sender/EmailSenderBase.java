package br.gov.es.siscap.utils.email.sender;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import br.gov.es.siscap.utils.email.builder.EmailBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Component
public class EmailSenderBase {

    @Autowired
    protected JavaMailSender sender;

    @Value("classpath:static/imagens/govES-logo.png")
    private Resource logoGov;

    @Value("classpath:static/imagens/icon-siscap-white.png")
    private Resource logoSiscap;

    protected MimeMessageHelper criarMensagemComPadroes() throws MessagingException, UnsupportedEncodingException {

        MimeMessage mensagem = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);
        
        helper.setFrom("naoresponder@siscap.es.gov.br", "SISCAP");
        
        // helper.addInline("govESlogo", logoGov);
        // helper.addInline("iconsiscapwhite", logoSiscap);
        // // helper.addInline("workAround", logoGov); // inclui esse 3 inline para evitar bug de quebrar a segunda imagem..

        ClassPathResource imagemLogoES =
						new ClassPathResource("static/imagens/govES-logo.png");
				helper.addInline("govESlogo", imagemLogoES);

				ClassPathResource imagemLogoSiscap =
						new ClassPathResource("static/imagens/icon-siscap-white.png");
				helper.addInline("Iconsiscap", imagemLogoSiscap);

        return helper;

    }

    protected boolean enviarParaLista(MimeMessageHelper helper, List<String> destinatarios) {
        for (String destino : destinatarios) {
            try {
                helper.setTo(destino);
                sender.send(helper.getMimeMessage());
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public boolean enviarEmail(EmailBuilder builder, List<String> emails) {

        try {

            MimeMessageHelper helper = criarMensagemComPadroes();

            helper.setSubject(builder.montarAssuntoEmail());
            helper.setText(builder.montarCorpoEmail(), true);

            // MimeMultipart multipart = new MimeMultipart("related");

            // // Parte HTML
            // MimeBodyPart htmlPart = new MimeBodyPart();
            // htmlPart.setContent(builder.montarCorpoEmail(), "text/html; charset=UTF-8");
            // multipart.addBodyPart(htmlPart);

            // // Imagem 1
            // MimeBodyPart img1 = new MimeBodyPart();
            // img1.attachFile((File) logoGov);
            // img1.setContentID("<govES-logo>");
            // img1.setDisposition(MimeBodyPart.INLINE);
            // multipart.addBodyPart(img1);

            // // Imagem 2
            // MimeBodyPart img2 = new MimeBodyPart();
            // img2.attachFile((File) logoSiscap);
            // img2.setContentID("<icon-siscap-white>");
            // img2.setDisposition(MimeBodyPart.INLINE);
            // multipart.addBodyPart(img2);

            // msg.setContent(multipart);
            // msg.setFrom(new InternetAddress("naoresponder@siscap.es.gov.br", "SISCAP"));

            return enviarParaLista( helper, emails );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail", e);
        }

    }

}


