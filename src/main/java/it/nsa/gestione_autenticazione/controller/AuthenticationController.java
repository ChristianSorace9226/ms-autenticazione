package it.nsa.gestione_autenticazione.controller;

import it.nsa.gestione_autenticazione.dto.request.RequestLoginDTO;
import it.nsa.gestione_autenticazione.service.resource.AuthenticationResource;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthenticationController {
    private final AuthenticationResource authenticationResource;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody RequestLoginDTO requestLoginDTO) {
        try {
            return ResponseEntity.ok(authenticationResource.login(requestLoginDTO));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader String token) {
        return ResponseEntity.ok(authenticationResource.logout(token));
    }
}
