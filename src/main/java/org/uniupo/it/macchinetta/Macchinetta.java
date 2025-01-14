package org.uniupo.it.macchinetta;

import java.sql.Timestamp;

public class Macchinetta {
    private String id_macchinetta;
    private int id_istituto;
    private StatusMacchinetta status;
    private Timestamp data_ultima_manutenzione;
    private Timestamp data_ultimo_refill;
    private Timestamp data_ultimo_svuotamento_cassa;
    private Timestamp data_installazione;
    private String piano;

    public Macchinetta(String id_macchinetta, int id_istituto, StatusMacchinetta status, Timestamp data_ultima_manutenzione, Timestamp data_ultimo_refill, Timestamp data_ultimo_svuotamento_cassa, Timestamp data_installazione, String piano) {
        this.id_macchinetta = id_macchinetta;
        this.id_istituto = id_istituto;
        this.status = status;
        this.data_ultima_manutenzione = data_ultima_manutenzione;
        this.data_ultimo_refill = data_ultimo_refill;
        this.data_ultimo_svuotamento_cassa = data_ultimo_svuotamento_cassa;
        this.data_installazione = data_installazione;
        this.piano = piano;
    }

    public Macchinetta(String idMacchinetta, int idIstituto, String piano) {
        this.id_macchinetta = idMacchinetta;
        this.id_istituto = idIstituto;
        this.piano = piano;
    }


    public String getId_macchinetta() {
        return id_macchinetta;
    }

    public void setId_macchinetta(String id_macchinetta) {
        this.id_macchinetta = id_macchinetta;
    }

    public int getId_istituto() {
        return id_istituto;
    }

    public void setId_istituto(int id_istituto) {
        this.id_istituto = id_istituto;
    }

    public StatusMacchinetta getStatus() {
        return status;
    }

    public void setStatus(StatusMacchinetta status) {
        this.status = status;
    }

    public Timestamp getData_ultima_manutenzione() {
        return data_ultima_manutenzione;
    }

    public void setData_ultima_manutenzione(Timestamp data_ultima_manutenzione) {
        this.data_ultima_manutenzione = data_ultima_manutenzione;
    }

    public Timestamp getData_ultimo_refill() {
        return data_ultimo_refill;
    }

    public void setData_ultimo_refill(Timestamp data_ultimo_refill) {
        this.data_ultimo_refill = data_ultimo_refill;
    }

    public Timestamp getData_ultimo_svuotamento_cassa() {
        return data_ultimo_svuotamento_cassa;
    }

    public void setData_ultimo_svuotamento_cassa(Timestamp data_ultimo_svuotamento_cassa) {
        this.data_ultimo_svuotamento_cassa = data_ultimo_svuotamento_cassa;
    }

    public Timestamp getData_installazione() {
        return data_installazione;
    }

    public void setData_installazione(Timestamp data_installazione) {
        this.data_installazione = data_installazione;
    }

    public String getPiano() {
        return piano;
    }

    public void setPiano(String piano) {
        this.piano = piano;
    }

    @Override
    public String toString() {
        return "Macchinetta{" +
                "id_macchinetta='" + id_macchinetta + '\'' +
                ", id_istituto=" + id_istituto +
                ", status=" + status +
                ", data_ultima_manutenzione=" + data_ultima_manutenzione +
                ", data_ultimo_refill=" + data_ultimo_refill +
                ", data_ultimo_svuotamento_cassa=" + data_ultimo_svuotamento_cassa +
                ", data_installazione=" + data_installazione +
                ", piano='" + piano + '\'' +
                '}';
    }
}
