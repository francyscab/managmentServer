package org.uniupo.it.istituto;

import com.google.gson.Gson;
import spark.Route;

import java.net.URLDecoder;
import java.util.List;

import static org.uniupo.it.Application.daoIstituto;
import static org.uniupo.it.Application.gson;

public class IstitutoController {

    public static Route getIstituti = (req, res) -> {
        res.type("application/json");
        List<Istituto> istituti = daoIstituto.getAllIstituti();
        return gson.toJson(istituti);
    };

    public static Route addIstituto = (req, res) -> {
        res.type("application/json");
        Istituto istituto = new Istituto();
        istituto.setNome(req.queryParams("nome"));
        istituto.setIndirizzo(req.queryParams("indirizzo"));
        istituto.setCitta(req.queryParams("citta"));
        daoIstituto.addIstituto(istituto);
        res.status(200);
        return "Istituto aggiunto";
    };

    public static Route getIstitutoById = (req, res) -> {
        res.type("application/json");
        int id = Integer.parseInt(req.params(":id"));
        Istituto istituto = daoIstituto.getIstitutoById(id);
        if (istituto == null) {
            res.status(404);
            return "Istituto non trovato";
        }
        return gson.toJson(istituto);
    };
}
