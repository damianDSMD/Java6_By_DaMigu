package org.example.lab1.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.lab1.config.JwtKeyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Service
public class JwtService {

    @Autowired
    private JwtKeyProvider keyProvider;

    /**
     * Create JWT signed with ES256 (ECDSA using P-256 and SHA-256)
     *
     * @param user          UserDetails
     * @param expirySeconds expiry in seconds
     * @return jwt string
     */
    public String create(UserDetails user, int expirySeconds) {
        long now = System.currentTimeMillis();
        PrivateKey privateKey = keyProvider.loadPrivateKey();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getAuthorities())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirySeconds * 1000L))
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact();
    }

    /**
     * Parse and return claims (throws on invalid signature)
     */
    public Claims getBody(String token) {
        PublicKey publicKey = keyProvider.loadPublicKey();
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validate(Claims claims) {
        return claims.getExpiration().after(new Date());
    }
}