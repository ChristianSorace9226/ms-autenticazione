package it.nsa.gestione_autenticazione.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLoginDTO extends BaseResponseDTO {
    String token;
}
