package br.gov.es.siscap.config.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

	@Value("${spring.mail.host}")
	private String HOST;

	@Value("${spring.mail.port}")
	private String PORT;

	@Value("${spring.mail.username}")
	private String USERNAME;

	@Value("${spring.mail.password}")
	private String PASSWORD;

	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String AUTH;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String STARTTLS_ENABLE;

	@Bean
	public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
		mailSenderImpl.setHost(HOST);
		mailSenderImpl.setPort(Integer.parseInt(PORT));
		mailSenderImpl.setUsername(USERNAME);
		mailSenderImpl.setPassword(PASSWORD);

		Properties props = mailSenderImpl.getJavaMailProperties();
		props.put("mail.smtp.auth", AUTH);
		props.put("mail.smtp.starttls.enable", STARTTLS_ENABLE);

		return mailSenderImpl;
	}
}