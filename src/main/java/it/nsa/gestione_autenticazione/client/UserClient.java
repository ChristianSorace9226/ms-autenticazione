package it.nsa.gestione_autenticazione.client;

import it.nsa.gestione_autenticazione.dto.UtenteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8081") //todo:URL del microservizio da chiedere a Claudio
public interface UserClient {

    @GetMapping("/api/users/exists") //todo: endpoint da chiedere a claudio
    Boolean doesUserExistByEmail(@RequestParam("email") String email);

    @GetMapping("/api/users/getUsername") //todo: endpoint da chiedere a claudio
    String getUsernameByEmail(@RequestParam("email") String email);

    @GetMapping("/users/{username}")
    UtenteDTO getUserDetails(@PathVariable("username") String username);
}



