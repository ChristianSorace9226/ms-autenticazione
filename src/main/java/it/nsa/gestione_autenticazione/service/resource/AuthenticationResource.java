package it.nsa.gestione_autenticazione.service.resource;

import it.nsa.gestione_autenticazione.dto.request.RequestLoginDTO;
import it.nsa.gestione_autenticazione.dto.response.ResponseGetStatoDTO;
import it.nsa.gestione_autenticazione.dto.response.ResponseLoginDTO;
import it.nsa.gestione_autenticazione.dto.response.ResponseLogoutDTO;

public interface AuthenticationResource {
    public ResponseLoginDTO login(RequestLoginDTO requestLoginDTO);
    public ResponseLogoutDTO logout(String token);
    public ResponseGetStatoDTO getStato(Long idUtente);
}
