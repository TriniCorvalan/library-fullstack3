package com.example.library.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Valida tokens emitidos por ms-usuarios (mismo jwt.secret).
 * No genera tokens: el login vive en el microservicio de usuarios.
 */
@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	public String extractEmail(String token) {
		return parseClaims(token).getSubject();
	}

	public String extractRol(String token) {
		return String.valueOf(parseClaims(token).get("rol"));
	}

	public boolean isTokenValid(String token) {
		try {
			return !parseClaims(token).getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}
