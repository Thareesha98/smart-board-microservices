package com.sbms.sbms_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.sbms.sbms_backend.repository.UserRepository;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
	
	
	@Autowired
	private UserRepository userRepository;


    // Generate a secure key (at least 256 bits). For now hardcode; later read from application.yml
    private static final String SECRET_KEY =
            "S2JQZVNoVm1ZcTN0Nnc5eiRDJkUpSEBNY1FmVGpXblo="; // example hex string

    private long jwtExpirationMs = 1000 * 60 * 60 * 24; // 24 hours

    // ---------------------------------------------
    // PUBLIC API
    // ---------------------------------------------

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        return generateToken(claims, userDetails);
//    }
    
    
    
    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        // IMPORTANT: Add role claim
        claims.put("role", 
            userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER")
        );

        return generateToken(claims, userDetails);
    }

    
    

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // ---------------------------------------------
    // INTERNAL HELPERS
    // ---------------------------------------------

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        // Load User entity from DB using email (subject)
        com.sbms.sbms_backend.model.User user =
                userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found for JWT"));

        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("email", user.getEmail());

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
