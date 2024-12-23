package com.Cards.Cards.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "mySuperSecretKeyForJwtSigningMustBeLongEnough12345"; // La misma clave secreta del Users Service

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractUserId(String token) {
        Object userId = extractClaims(token).get("userId"); // Leer el claim 'userId'

        // Convertir a String si es Integer
        if (userId instanceof Integer) {
            return String.valueOf(userId);
        }
        return (String) userId; // Retornar directamente si es String
    }



    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
