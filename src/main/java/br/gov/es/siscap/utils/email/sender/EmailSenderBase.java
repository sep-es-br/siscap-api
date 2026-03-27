package br.gov.es.siscap.utils.email.sender;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    @Value("classpath:static/imagens/siscap-white.png")
    private Resource logoSiscap;

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
