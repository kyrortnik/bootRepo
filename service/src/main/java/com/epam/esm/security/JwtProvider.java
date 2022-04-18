package com.epam.esm.security;

import com.epam.esm.Role;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Utility Class for common Java Web Token operations
 */
@Component
public class JwtProvider {

    private static final String ROLES_KEY = "roles";
    private static final String AUTHORITY = "authority";

    private final String secretKey;
    private final long validityInMilliseconds;

    @Autowired
    public JwtProvider(@Value("${security.jwt.token.secret-key}") String secretKey,
                       @Value("${security.jwt.token.expiration}") long validityInMilliseconds) {

        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
    }

    /**
     * Create JWT string given username and roles.
     *
     * @param username
     * @param roles
     * @return jwt string
     */
    public String createToken(String username, List<Role> roles) {
        //Add the username to the payload
        Claims claims = Jwts.claims().setSubject(username);
        //Convert roles to Spring Security SimpleGrantedAuthority objects,
        //Add to Simple Granted Authority objects to claims
        claims.put(ROLES_KEY, roles.stream().map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        //Build the Token
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Validate the JWT String
     *
     * @param token JWT string
     * @return true if valid and not expired, false otherwise
     */
    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            Date expirationTime = claimsJws.getBody().getExpiration();
            Date currentTime = new Date();

            return currentTime.before(expirationTime);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Get the username from the token string
     *
     * @param token jwt
     * @return username
     */
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Get the roles from the token string
     *
     * @param token jwt
     * @return username
     */
    public List<GrantedAuthority> getRoles(String token) {
        List<Map<String, String>> roleClaims = (List<Map<String, String>>) Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token).getBody().get(ROLES_KEY);
        return roleClaims.stream().map(roleClaim ->
                new SimpleGrantedAuthority(roleClaim.get(AUTHORITY)))
                .collect(Collectors.toList());
    }
}
