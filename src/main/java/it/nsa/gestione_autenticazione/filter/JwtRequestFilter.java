package it.nsa.gestione_autenticazione.filter;

import it.nsa.gestione_autenticazione.client.UserClient;
import it.nsa.gestione_autenticazione.dto.UtenteDTO;
import it.nsa.gestione_autenticazione.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserClient userClient;

    public JwtRequestFilter(JwtUtil jwtUtil, UserClient userClient) {
        this.jwtUtil = jwtUtil;
        this.userClient = userClient;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Estraggo il token JWT dalla request tramite metodo separato dal filtro
        String jwt = extractJwtFromRequest(request);

        // Verifica che il token non sia null
        if (jwt != null) {
            // Estrai lo username dal token JWT
            String username = jwtUtil.extractUsername(jwt);

            // Verifica che il token sia valido per l'username estratto
            if (username != null && jwtUtil.validateToken(jwt, username)) {

                // todo: utilizzo feign per caricare i dettagli dell'utente
                UtenteDTO utenteDTO = userClient.getUserDetails(username);

                // Crea l'oggetto di autenticazione con le authorities dell'utente
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        utenteDTO, null, null);

                // Imposta l'autenticazione nel contesto di sicurezza
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continua il filtro nella chain
        chain.doFilter(request, response);
    }

    // Estrae il token JWT dalla request
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Rimuove "Bearer " dal token
        }
        return null;
    }
}
