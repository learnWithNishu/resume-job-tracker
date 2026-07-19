package com.resume.job.tracker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;


  private SecretKey getSecretKey(){
      return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));

  }
  public String generateToken(String email){
      return Jwts.builder()
              .subject(email)
              .issuedAt(new Date())
              .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
              .signWith(getSecretKey())
              .compact(); // build and return token String
  }

  public String extractEmail(String token){
      return extractAllClaims(token).getSubject();
  }

  public boolean validateToken(String token, String expectedEmail){
      String emailFromToken = extractEmail(token);
      return emailFromToken.equals(expectedEmail) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token){
      return extractAllClaims(token).getExpiration().before(new Date());
  }
  private Claims extractAllClaims(String token){
      return Jwts.parser()
              .verifyWith(getSecretKey())
              .build()
              .parseSignedClaims(token)
              .getPayload();
  }

}
