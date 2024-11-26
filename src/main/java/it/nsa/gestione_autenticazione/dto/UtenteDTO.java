package it.nsa.gestione_autenticazione.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtenteDTO {
    private Long idUtente;
    private String username;
    private String password;
    private Set<String> ruoli;
}
