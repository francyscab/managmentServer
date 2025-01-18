package org.uniupo.it.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.uniupo.it.Application;
import spark.Filter;
import spark.Request;
import spark.Response;

public class AuthMiddleware {

    private static final JwtValidator jwtValidator = new JwtValidator();

    public static Filter authenticate = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        response.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS");
        response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.type("application/json");

        if (request.requestMethod().equals("OPTIONS")) {
            return;
        }

        String authHeader = request.headers("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.status(401);
            response.body(Application.gson.toJson(new ErrorResponse("Token mancante o non valido")));
            throw new UnauthorizedException("Token mancante o non valido");
        }

        String token = authHeader.substring(7);
        try {
            DecodedJWT jwt = jwtValidator.validate(token);

            if (!jwtValidator.hasValidRole(jwt)) {
                response.status(403);
                response.body(Application.gson.toJson(new ErrorResponse("Ruolo non autorizzato")));
                throw new ForbiddenException("Ruolo non autorizzato");
            }

            request.attribute("jwt", jwt);
            request.attribute("userRole", jwtValidator.extractRole(jwt));

        } catch (Exception e) {
            response.status(401);
            response.body(Application.gson.toJson(new ErrorResponse("Token non valido")));
            throw new UnauthorizedException("Token non valido");
        }
    };

    public static Filter requireAdmin = (Request request, Response response) -> {
        response.type("application/json");
        DecodedJWT jwt = request.attribute("jwt");
        if (!jwtValidator.isAdmin(jwt)) {
            response.status(403);
            response.body(Application.gson.toJson(new ErrorResponse("Questa operazione richiede privilegi di amministratore")));
            throw new ForbiddenException("Questa operazione richiede privilegi di amministratore");
        }
    };

    public static Filter requireImpiegato = (Request request, Response response) -> {
        response.type("application/json");
        DecodedJWT jwt = request.attribute("jwt");
        if (!jwtValidator.isImpiegato(jwt)) {
            response.status(403);
            response.body(Application.gson.toJson(new ErrorResponse("Questa operazione richiede privilegi di impiegato")));
            throw new ForbiddenException("Questa operazione richiede privilegi di impiegato");
        }
    };
}