package it.nsa.gestione_autenticazione.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class RequestLoginDTO {

    @Email(message = "L'email deve essere una mail valida")
    String email;

    @NotBlank(message = "Il campo password non può essere vuoto")
    String password;

}
