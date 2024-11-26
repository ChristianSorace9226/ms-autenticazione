package it.nsa.gestione_autenticazione;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GestioneAutenticazioneApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestioneAutenticazioneApplication.class, args);
	}

}
