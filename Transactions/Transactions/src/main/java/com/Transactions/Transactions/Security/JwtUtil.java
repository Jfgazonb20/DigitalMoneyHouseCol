package com.Transactions.Transactions.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "mySuperSecretKeyForJwtSigningMustBeLongEnough12345"; // Clave secreta

    // Extraer todos los claims del token
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    // Extraer el username (subject) del token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extraer el userId del token
    public String extractUserId(String token) {
        Object userId = extractClaims(token).get("userId"); // Leer el claim 'userId'

        if (userId instanceof Integer) {
            return String.valueOf(userId);
        }
        return (String) userId; // Retornar directamente si es String
    }

    // Validar si el token es válido
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
