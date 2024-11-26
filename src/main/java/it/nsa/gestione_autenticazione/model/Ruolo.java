package it.nsa.gestione_autenticazione.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ruolo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String nome;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ruolo_permesso",
            joinColumns = @JoinColumn(name = "ruolo_id"),
            inverseJoinColumns = @JoinColumn(name = "permesso_id")
    )
    Set<Permesso> permessi;
}
