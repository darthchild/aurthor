package com.darthchild.aurthor.JWT;

import com.darthchild.aurthor.model.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    /**
     * Generates a new secret key for signing JWT tokens
     * @return HMAC-SHA256 signing key
     */
    private Key getKey(){
        KeyGenerator keyGen;
        try {
            keyGen = KeyGenerator.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKey sk = keyGen.generateKey();
        return Keys.hmacShaKeyFor(sk.getEncoded());
    }

    /**
     * Generates a JWT token for a given username and additional claims
     * @return signed JWT as a compact string
     */
    public String generateToken(String username, Map<String, Object> claims){
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 180 * 1000 * 60)) // valid for 3hrs
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token){
        return "";
    }

    public Boolean validateToken(String token, UserPrincipal user){
        return true;
    }
}
