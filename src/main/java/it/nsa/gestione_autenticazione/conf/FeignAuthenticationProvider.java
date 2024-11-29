package it.nsa.gestione_autenticazione.conf;

import it.nsa.common.dto.UtenteDTO;
import it.nsa.gestione_autenticazione.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class FeignAuthenticationProvider implements AuthenticationProvider {

    private final UserClient userClient;

    public FeignAuthenticationProvider(UserClient userClient) {
        this.userClient = userClient;
    }


    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        UtenteDTO utente = null;
        try {
            utente = userClient.getUtenteDTO(username).getBody();
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }

        if (utente == null) {
            throw new UsernameNotFoundException("Utente non trovato con username: " + username);
        }

        if (!password.equals(utente.getPassword())) {
            log.error("Credenziali non valide");
            throw new BadCredentialsException("Credenziali non valide");
        }

        // Crea un oggetto Authentication valido se la password è corretta
        UserDetails userDetails = new User(
                utente.getNome(),
                utente.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // Di default
        );
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

