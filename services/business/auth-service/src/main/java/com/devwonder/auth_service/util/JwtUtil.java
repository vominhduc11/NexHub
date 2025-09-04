package com.devwonder.auth_service.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.devwonder.auth_service.exception.TokenGenerationException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Component
@Slf4j
public class JwtUtil {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final String keyId = UUID.randomUUID().toString();

    @Value("${jwt.expiration:86400}") // 24 hours in seconds
    private Long expiration;

    public JwtUtil() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
            this.publicKey = (RSAPublicKey) keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new TokenGenerationException("Error generating RSA key pair", e);
        }
    }

    public String generateToken(Long accountId, String username, String userType, Set<String> roles, Set<String> permissions) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setSubject(String.valueOf(accountId))
                .claim("username", username)
                .claim("userType", userType)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .setIssuer("auth-service")
                .setAudience("nexhub-services")
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Map<String, Object> getJwks() {
        Map<String, Object> jwks = new HashMap<>();
        List<Map<String, Object>> keys = new ArrayList<>();
        
        Map<String, Object> key = new HashMap<>();
        key.put("kty", "RSA");
        key.put("use", "sig");
        key.put("kid", keyId);
        key.put("alg", "RS256");
        
        // Convert RSA public key to JWK format
        key.put("n", Base64.getUrlEncoder().withoutPadding()
                .encodeToString(publicKey.getModulus().toByteArray()));
        key.put("e", Base64.getUrlEncoder().withoutPadding()
                .encodeToString(publicKey.getPublicExponent().toByteArray()));
        
        keys.add(key);
        jwks.put("keys", keys);
        
        return jwks;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }

    public Long getAccountIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getUserTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userType", String.class);
    }

    public boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().before(new Date());
    }
}