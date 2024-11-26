package it.nsa.gestione_autenticazione.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import it.nsa.gestione_autenticazione.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        this.SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // Crea un JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 minuti di validità
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Firmo con la chiave
                .compact();
    }

    // Estrae il nome utente dal token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Estrae la data di scadenza dal token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Metodo per estrarre un claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Estrae tutti i claims dal token
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY) // Imposto la chiave di firma
                    .build()
                    .parseClaimsJws(token) // Analizza il JWT
                    .getBody(); // Ottengo il corpo (Claims)
        } catch (Exception e) {
            throw new InvalidTokenException("Token JWT non valido o errore nel parsing", e);
        }
    }

    // Verifica se il token è scaduto
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Valida il token controllando se il nome utente e la scadenza sono corretti
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    // Estrae l'ID dell'utente dal JWT
    public String getUserIdFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); //e qui l'id?
    }

}
