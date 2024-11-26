package it.nsa.gestione_autenticazione.service.impl;

import it.nsa.gestione_autenticazione.client.UserClient;
import it.nsa.gestione_autenticazione.common.Stato;
import it.nsa.gestione_autenticazione.dto.UtenteDTO;
import it.nsa.gestione_autenticazione.dto.request.RequestLoginDTO;
import it.nsa.gestione_autenticazione.dto.response.ResponseLoginDTO;
import it.nsa.gestione_autenticazione.dto.response.ResponseLogoutDTO;
import it.nsa.gestione_autenticazione.model.Jwt;
import it.nsa.gestione_autenticazione.model.repository.JwtRepository;
import it.nsa.gestione_autenticazione.service.resource.AuthenticationResource;
import it.nsa.gestione_autenticazione.util.JwtUtil;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
        Jwt tokenEntity = new Jwt();

        String usernameUtente = userClient.getUsernameByEmail(requestLoginDTO.getEmail());

        if (usernameUtente != null) {
            UtenteDTO utenteDTO = userClient.getUserDetails(usernameUtente);
            Long idUtente = utenteDTO.getIdUtente();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameUtente, utenteDTO.getPassword()));

            String token = jwtUtil.generateToken(usernameUtente);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            tokenEntity.setToken(token);
            tokenEntity.setIdUtente(idUtente);

            // Salvo l'oggetto Jwt nel repository per tracciare gli utenti loggati
            jwtRepository.save(tokenEntity);

            // Setto l'oggetto response
            responseLoginDTO.setToken(token);
            responseLoginDTO.setStato(Stato.SUCCESS);

            return responseLoginDTO;
        } else {
            throw new UsernameNotFoundException("Username non valido");
        }
    }

    @Override
    public ResponseLogoutDTO logout(String token) {
        try {
            // Estrazione del token JWT dall'header Authorization
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

}

