package it.nsa.gestione_autenticazione.service.impl;

import it.nsa.common.dto.UtenteDTO;
import it.nsa.gestione_autenticazione.client.UserClient;
import it.nsa.gestione_autenticazione.common.Stato;
import it.nsa.gestione_autenticazione.dto.request.RequestLoginDTO;
import it.nsa.gestione_autenticazione.dto.response.ResponseGetStatoDTO;
import it.nsa.gestione_autenticazione.dto.response.ResponseLoginDTO;
import it.nsa.gestione_autenticazione.dto.response.ResponseLogoutDTO;
import it.nsa.gestione_autenticazione.exception.ExternalCallException;
import it.nsa.gestione_autenticazione.model.Jwt;
import it.nsa.gestione_autenticazione.model.repository.JwtRepository;
import it.nsa.gestione_autenticazione.service.resource.AuthenticationResource;
import it.nsa.gestione_autenticazione.util.JwtUtil;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Data
public class AuthenticationServiceImpl implements AuthenticationResource {

    private final JwtRepository jwtRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserClient userClient;

    @Override
    public ResponseLoginDTO login(RequestLoginDTO requestLoginDTO) {
        try {
            UtenteDTO utenteDTO = userClient.getUserByEmail(requestLoginDTO.getEmail()).getBody();

            if (utenteDTO != null) {
                UserDetails userDetails = User.withUsername(utenteDTO.getNome())
                        .password(utenteDTO.getPassword()).build();

                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword()));

                String token = jwtUtil.generateToken(utenteDTO.getNome());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
                Jwt tokenEntity = new Jwt();

                tokenEntity.setToken(token);
                tokenEntity.setIdUtente(utenteDTO.getIdUtente());

                // Salvo l'oggetto Jwt nel repository per tracciare gli utenti loggati
                jwtRepository.save(tokenEntity);

                // Setto l'oggetto response
                responseLoginDTO.setToken(token);
                responseLoginDTO.setStato(Stato.SUCCESS);

                return responseLoginDTO;
            }
            throw new RuntimeException("UtenteDTO = NULL");
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("Utente con email fornita non trovato");
        }
    }

    @Override
    public ResponseLogoutDTO logout(String token) {
        try {
            String jwtToken = token.substring(7);

            if (jwtRepository.findByToken(jwtToken).isPresent()) {
                jwtRepository.deleteByToken(jwtToken);
            }

            // Pulisco il contesto di sicurezza
            SecurityContextHolder.clearContext();

            ResponseLogoutDTO responseLogoutDTO = new ResponseLogoutDTO();
            responseLogoutDTO.setStato(Stato.SUCCESS);
            return responseLogoutDTO;

        } catch (Exception e) {
            throw new RuntimeException("Logout non riuscito!");
        }

    }

    @Override
    public ResponseGetStatoDTO getStato(Long idUtente) {
        try {
            ResponseGetStatoDTO responseGetStatoDTO = new ResponseGetStatoDTO();

            if (jwtRepository.getJwtByIdUtente(idUtente).isPresent()) {
                responseGetStatoDTO.setStato(Stato.UTENTE_AUTENTICATO);
            } else {
                responseGetStatoDTO.setStato(Stato.NO_AUTENTCAZIONE);
            }
            responseGetStatoDTO.setIdUtente(idUtente);
            return responseGetStatoDTO;
        } catch (ExternalCallException e) {
            throw new ExternalCallException("Chiamata al servizio esterno fallita");
        }

    }

}

