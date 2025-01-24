package org.uniupo.it;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import org.uniupo.it.istituto.DaoIstitutoImpl;
import org.uniupo.it.istituto.IstitutoController;
import org.uniupo.it.macchinetta.MacchinettaController;
import org.uniupo.it.manutenzione.ManutenzioneController;
import org.uniupo.it.mqtt.MQTTConnection;
import org.uniupo.it.ricavo.RicavoController;
import org.uniupo.it.util.*;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Application {
    private static final Dotenv dotenv = Dotenv.configure().load();
    public static DaoIstitutoImpl daoIstituto;
    public static Gson gson;

    public static void main(String[] args) {

        daoIstituto = new DaoIstitutoImpl();
        gson = new Gson();

        port(Integer.parseInt(dotenv.get("SERVER_PORT")));
        enableDebugScreen();
        String keystoreFile = dotenv.get("KEYSTORE_FILE");
        String keystorePassword = dotenv.get("KEYSTORE_PASSWORD");

        secure(keystoreFile, keystorePassword, null, null);
        before((req, res) -> System.out.println("Richiesta ricevuta: " + req.requestMethod() + " " + req.url()));
        before("/*", AuthMiddleware.authenticate);

        path(Path.Web.IstitutiBasePath, () -> {
            get("", IstitutoController.getIstituti);
            get(Path.Web.GET_SCHOOL_BY_ID, IstitutoController.getIstitutoById);

            before("", (request, response) -> {
                if ("POST".equalsIgnoreCase(request.requestMethod())) {
                    AuthMiddleware.requireAdmin.handle(request, response);
                }
            });

            before("/:id", (request, response) -> {
                if ("DELETE".equalsIgnoreCase(request.requestMethod())) {
                    AuthMiddleware.requireAdmin.handle(request, response);
                }
            });

            post("", IstitutoController.addIstituto);
            delete(Path.Web.GET_SCHOOL_BY_ID, IstitutoController.deleteIstituto);
            get(Path.Web.GET_MACCHINETTE_BY_ISTITUTO, MacchinettaController.getMacchinetteByIstituto);

            before("/:id/macchinette", (request, response) -> {
                if ("POST".equalsIgnoreCase(request.requestMethod())) {
                    AuthMiddleware.requireAdmin.handle(request, response);
                }
            });

            post(Path.Web.ADD_MACCHINETTA_TO_ISTITUTO, MacchinettaController.addMacchinetta);
        });

        path(Path.Web.MacchinetteBasePath, () -> {
            get("", MacchinettaController.getAllMacchinette);
            get(Path.Web.GET_MACCHINETTA_BY_ID, MacchinettaController.getMacchinettaById);

            // Middleware per eliminazione macchinetta
            before("/:id", (request, response) -> {
                if ("DELETE".equalsIgnoreCase(request.requestMethod())) {
                    AuthMiddleware.requireAdmin.handle(request, response);
                }
            });

            delete(Path.Web.GET_MACCHINETTA_BY_ID, MacchinettaController.deleteMacchinetta);
        });

        path(Path.Web.RicaviBasePath, () -> {
            // Rotte di lettura accessibili a tutti gli utenti autenticati
            get("", RicavoController.getAllRicavi);
            get(Path.Web.GET_RICAVI_BY_MACCHINETTA, RicavoController.getRicaviByMacchinetta);
            get(Path.Web.GET_RICAVI_BY_ISTITUTO, RicavoController.getRicaviByIstituto);
            get(Path.Web.GET_TOTALE_RICAVI, RicavoController.getTotaleRicavi);
            get(Path.Web.GET_TOTALE_RICAVI_BY_MACCHINETTA, RicavoController.getTotaleRicaviByMacchinetta);
            get(Path.Web.GET_TOTALE_RICAVI_BY_ISTITUTO, RicavoController.getTotaleRicaviByIstituto);

            before("/svuota", (request, response) -> {
                if ("GET".equalsIgnoreCase(request.requestMethod())) {
                    AuthMiddleware.requireAdmin.handle(request, response);
                }
            });

            get(Path.Web.SVUOTA_RICAVI, RicavoController.svuotaRicavi);
        });

        path(Path.Web.ManutenzioniBasePath, () -> {
            before("", (request, response) -> {
                if ("POST".equalsIgnoreCase(request.requestMethod())) {
                    AuthMiddleware.requireAdmin.handle(request, response);
                }
            });
            post(Path.Web.RICHIEDI_MANUTENZIONE, ManutenzioneController.richiediManutenzione);
        });

        /*path(Path.Web.ScorteBasePath, () -> {
            get(Path.Web.GET_SCORTE_MACCHINETTA, ScorteController.getScorteByMacchinetta);
        });*/

        notFound((req, res) -> {
            res.status(404);
            return "Pagina non trovata";
        });
        exception(UnauthorizedException.class, (e, request, response) -> {
            response.status(401);
            response.type("application/json");
            response.body(gson.toJson(new ErrorResponse(e.getMessage())));
        });

        exception(ForbiddenException.class, (e, request, response) -> {
            response.status(403);
            response.type("application/json");
            response.body(gson.toJson(new ErrorResponse(e.getMessage())));
        });

        exception(Exception.class, (e, request, response) -> {
            response.status(500);
            response.type("application/json");
            response.body(gson.toJson(new ErrorResponse("Errore interno del server")));
        });

        try {
            MQTTConnection.getInstance().publish("/samello/pisello", "Ciao");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}