package com.digitalmoneyhouse.util;

import com.digitalmoneyhouse.model.User;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "clave-secreta"; // En producción, usar propiedades o variables de entorno
    private final long EXPIRATION_TIME = 86400000; // 24 horas

    public String generateToken(User user) {
        return Jwts.builder()
                   .setSubject(user.getEmail())
                   .claim("id", user.getId())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                   .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes())
                   .compact();
    }

    public Claims validateToken(String token) throws JwtException {
        return Jwts.parser()
                   .setSigningKey(SECRET_KEY.getBytes())
                   .parseClaimsJws(token)
                   .getBody();
    }
}

