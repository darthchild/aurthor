package com.darthchild.aurthor.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import com.darthchild.aurthor.model.UserPrincipal;

@Component
public class JwtUtils {

    private String secretKey = "";
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    public JwtUtils() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decodes the configured Base64-encoded secret key and generates a
     * {@link javax.crypto.SecretKey} instance for signing and verifying JWT tokens.
     * <p>
     * The method performs the following steps:
     * <ul>
     *   <li>Decodes the secret key from its Base64 representation</li>
     *   <li>Creates an HMAC-SHA key using the decoded bytes</li>
     *   <li>Logs the generated key in Base64URL format for debugging purposes</li>
     * </ul>
     *
     * @return the {@link SecretKey} derived from the Base64-encoded secret key,
     *         suitable for HMAC signing in JWT operations
     * @throws IllegalArgumentException if the secret key is invalid or
     *         cannot be decoded
     */
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        log.debug("JWT Secret Key (Base64URL): {}", Encoders.BASE64URL.encode(key.getEncoded()));
        return key;
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

    /**
     * Validates a JWT token by checking that the username in the token matches the
     * given <b>User</b> and token expiration
     */
    public boolean validateToken(String token, UserPrincipal userPrincipal) {
        final String username = extractUsername(token);
        return (username.equals(userPrincipal.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
