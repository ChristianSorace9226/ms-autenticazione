package it.nsa.gestione_autenticazione.dto.response;

import it.nsa.gestione_autenticazione.common.Stato;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponseDTO {
    Stato stato;
}
