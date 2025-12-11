package br.gov.es.siscap.utils.email.sender;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import br.gov.es.siscap.utils.email.builder.EmailBuilder;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailSenderBase {

    @Autowired
    protected JavaMailSender sender;

    @Value("classpath:static/imagens/govES-logo.png")
    private Resource logoGov;

    @Value("classpath:static/imagens/icon-siscap-white.png")
    private Resource logoSiscap;

    // protected MimeMessageHelper criarMensagemComPadroes() throws MessagingException, UnsupportedEncodingException {

    //     // MimeMessage mensagem = sender.createMimeMessage();
    //     // MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

    //     // helper.setFrom("gp.sep@sep.es.gov.br", "SISCAP");

    //     // ClassPathResource imagemLogoES =
    //     // new ClassPathResource("static/imagens/govES-logo.png");
    //     // helper.addInline("govESlogo", imagemLogoES);

    //     // ClassPathResource imagemLogoSiscap =
    //     // new ClassPathResource("static/imagens/icon-siscap-white.png");
    //     // helper.addInline("iconsiscapwhite", imagemLogoSiscap);

    //     MimeMessage mensagem = sender.createMimeMessage();

    //     // Garante estrutura de email compatível com Gmail
    //     MimeMessageHelper helper = new MimeMessageHelper(
    //             mensagem,
    //             MimeMessageHelper.MULTIPART_MODE_RELATED,
    //             "UTF-8");

    //     helper.setFrom("naoresponder@siscap.es.gov.br", "SISCAP");

    //     helper.addInline("govESlogo", logoGov);
    //     helper.addInline("iconsiscapwhite", logoSiscap);

    //     return helper;

    // }

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

            MimeMessage mensagem = sender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mensagem,
                    MimeMessageHelper.MULTIPART_MODE_RELATED,
                    "UTF-8");

            helper.setSubject(builder.montarAssuntoEmail());
            helper.setText(builder.montarCorpoEmail(), true);

            helper.setFrom("naoresponder@siscap.es.gov.br", "SISCAP");

            helper.addInline("govESlogo", logoGov);
            helper.addInline("iconsiscapwhite", logoSiscap);

            return enviarParaLista(helper, emails);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail", e);
        }

    }

}
