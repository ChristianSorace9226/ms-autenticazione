package it.nsa.gestione_autenticazione.conf;

import it.nsa.gestione_autenticazione.client.UserClient;
import it.nsa.gestione_autenticazione.dto.UtenteDTO;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class FeignAuthenticationProvider implements AuthenticationProvider {

    private final UserClient userClient;
    private final BCryptPasswordEncoder passwordEncoder;

    public FeignAuthenticationProvider(UserClient userClient, BCryptPasswordEncoder passwordEncoder) {
        this.userClient = userClient;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // Recupera i dettagli dell'utente dal microservizio tramite Feign
        UtenteDTO utente = userClient.getUserDetails(username);

        if (utente == null) {
            throw new UsernameNotFoundException("Utente non trovato con username: " + username);
        }

        // Confronta la password con quella memorizzata nel ms
        if (!passwordEncoder.matches(password, utente.getPassword())) {
            throw new BadCredentialsException("Credenziali non valide");
        }

        // Crea un oggetto Authentication valido se la password è corretta
        UserDetails userDetails = new User(
                utente.getUsername(),
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

