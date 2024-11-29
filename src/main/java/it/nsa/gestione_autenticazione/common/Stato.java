package it.nsa.gestione_autenticazione.common;

import lombok.Getter;

@Getter
public enum Stato {
    SUCCESS("success"),
    FAILED("failed"),
    UTENTE_AUTENTICATO("already authenticated"),
    NO_AUTENTCAZIONE("missing authentication");

    final String message;

    Stato(String message) {
        this.message = message;
    }
}
