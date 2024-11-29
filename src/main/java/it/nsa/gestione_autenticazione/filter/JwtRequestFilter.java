package it.nsa.gestione_autenticazione.filter;

import it.nsa.common.dto.UtenteDTO;
import it.nsa.gestione_autenticazione.client.UserClient;
import it.nsa.gestione_autenticazione.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
        String jwt = extractJwtFromRequest(request);
        if (jwt != null) {
            String username = jwtUtil.extractUsername(jwt);
            if (username != null && jwtUtil.validateToken(jwt, username)) {
                UtenteDTO utenteDTO = null;
                try {
                    utenteDTO = userClient.getUtenteDTO(username).getBody();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                assert utenteDTO != null;
                UserDetails userDetails = User.withUsername(utenteDTO.getNome()).password(utenteDTO.getPassword()).build();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
