package it.nsa.gestione_autenticazione.client;

import it.nsa.common.dto.UtenteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8085/feign")
public interface UserClient {

    @GetMapping("/get-utente-dto")
    public ResponseEntity<UtenteDTO> getUtenteDTO(@RequestParam String username);
    @GetMapping("/get-user")
    public ResponseEntity<UtenteDTO> getUserByEmail(@RequestParam("email") String email);
    @GetMapping("/get-utente-dto-by-id/{idUtente}")
    public ResponseEntity<UtenteDTO> getUtenteById(@PathVariable Long idUtente);

}



