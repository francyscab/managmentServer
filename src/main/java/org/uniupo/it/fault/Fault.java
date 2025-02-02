package org.uniupo.it.fault;

import java.sql.Timestamp;
import java.util.UUID;

public class Fault {
    private UUID idFault;
    private String idMacchinetta;
    private String descrizione;
    private Timestamp dataSegnalazione;
    private FaultType tipoGuasto;
    private int idIstituto;
    private boolean risolto;

    public Fault(UUID idFault, String idMacchinetta, String descrizione, Timestamp dataSegnalazione, FaultType tipoGuasto, int idIstituto, boolean risolto) {
        this.idFault = idFault;
        this.idMacchinetta = idMacchinetta;
        this.descrizione = descrizione;
        this.dataSegnalazione = dataSegnalazione;
        this.tipoGuasto = tipoGuasto;
        this.idIstituto = idIstituto;
        this.risolto = risolto;
    }

    public UUID getIdFault() {
        return idFault;
    }

    public void setIdFault(UUID idFault) {
        this.idFault = idFault;
    }

    public String getIdMacchinetta() {
        return idMacchinetta;
    }

    public void setIdMacchinetta(String idMacchinetta) {
        this.idMacchinetta = idMacchinetta;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Timestamp getDataSegnalazione() {
        return dataSegnalazione;
    }

    public void setDataSegnalazione(Timestamp dataSegnalazione) {
        this.dataSegnalazione = dataSegnalazione;
    }

    public FaultType getTipoGuasto() {
        return tipoGuasto;
    }

    public void setTipoGuasto(FaultType tipoGuasto) {
        this.tipoGuasto = tipoGuasto;
    }

    public int getIdIstituto() {
        return idIstituto;
    }

    public void setIdIstituto(int idIstituto) {
        this.idIstituto = idIstituto;
    }

    public boolean isRisolto() {
        return risolto;
    }

    public void setRisolto(boolean risolto) {
        this.risolto = risolto;
    }

    @Override
    public String toString() {
        return "Fault{" +
                "idFault=" + idFault +
                ", idMacchinetta='" + idMacchinetta + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", dataSegnalazione=" + dataSegnalazione +
                ", tipoGuasto=" + tipoGuasto +
                ", idIstituto=" + idIstituto +
                ", risolto=" + risolto +
                '}';
    }
}