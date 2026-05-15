package br.gov.es.siscap.config.mail;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

	@Value("${spring.mail.host}")
	private String host;

	@Value("${spring.mail.port}")
	private String port;

	@Value("${spring.mail.username:}")
	private String username;

	@Value("${spring.mail.password:}")
	private String password;

	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String auth;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String starttlsenable;

	@Bean
	public JavaMailSenderImpl mailSender() {

		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setHost(host);
		javaMailSenderImpl.setPort(Integer.parseInt(port));

		if (Boolean.parseBoolean(auth)) {
			javaMailSenderImpl.setUsername(username);
			javaMailSenderImpl.setPassword(password);
		}

		javaMailSenderImpl.setDefaultEncoding("UTF-8");

		Properties props = javaMailSenderImpl.getJavaMailProperties();
		props.put("mail.smtp.auth", auth);
		props.put("mail.smtp.starttls.enable", starttlsenable);

		return javaMailSenderImpl;
	}
}
