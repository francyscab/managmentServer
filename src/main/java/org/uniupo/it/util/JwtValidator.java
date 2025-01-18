package org.uniupo.it.util;


import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

public class JwtValidator {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidator.class);
    private static final List<String> allowedIsses = Collections.singletonList("http://127.0.0.1:8080/auth/realms/amministrazione-realm");
    private static final String CLIENT_ID = "my-amministrativo";

    private String getKeycloakCertificateUrl(DecodedJWT token) {
        return token.getIssuer() + "/protocol/openid-connect/certs";
    }

    private RSAPublicKey loadPublicKey(DecodedJWT token) throws JwkException, MalformedURLException {
        final String url = getKeycloakCertificateUrl(token);
        JwkProvider provider = new UrlJwkProvider(new URL(url));
        return (RSAPublicKey) provider.get(token.getKeyId()).getPublicKey();
    }

    public DecodedJWT validate(String token) {
        try {
            final DecodedJWT jwt = JWT.decode(token);

            if (!allowedIsses.contains(jwt.getIssuer())) {
                throw new InvalidParameterException(String.format("Unknown Issuer %s", jwt.getIssuer()));
            }

            RSAPublicKey publicKey = loadPublicKey(jwt);

            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwt.getIssuer())
                    .build();

            verifier.verify(token);
            return jwt;

        } catch (Exception e) {
            logger.error("Failed to validate JWT", e);
            throw new InvalidParameterException("JWT validation failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public String extractRole(DecodedJWT jwt) {
        try {
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access").asMap();
            System.out.println(resourceAccess);
            if (resourceAccess != null && resourceAccess.containsKey(CLIENT_ID)) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(CLIENT_ID);
                if (clientAccess != null) {
                    List<String> roles = (List<String>) clientAccess.get("roles");
                    if (roles != null && !roles.isEmpty()) {
                        return roles.get(0);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to extract role from JWT", e);
            return null;
        }
    }

    public boolean isAdmin(DecodedJWT jwt) {
        String role = extractRole(jwt);
        return "amministratore".equals(role);
    }

    public boolean isImpiegato(DecodedJWT jwt) {
        String role = extractRole(jwt);
        return "impiegato".equals(role);
    }

    public boolean hasValidRole(DecodedJWT jwt) {
        return isAdmin(jwt) || isImpiegato(jwt);
    }
}