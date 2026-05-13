package ru.blps.googleplay.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.blps.googleplay.config.AppSecurityProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class JwtService {

    private final SecretKey key;
    private final long ttlSeconds;

    public JwtService(AppSecurityProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
        this.ttlSeconds = properties.getJwt().getTtlSeconds();
    }

    public String issue(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        List<String> auth = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(ttlSeconds)))
            .claim("auth", auth)
            .signWith(key)
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}

