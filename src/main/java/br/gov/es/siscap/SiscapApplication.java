package br.gov.es.siscap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableFeignClients
public class SiscapApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiscapApplication.class, args);
	}

}
