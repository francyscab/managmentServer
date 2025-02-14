package org.uniupo.it.macchinetta;

public class Macchinetta {
    private String id_macchinetta;
    private int id_istituto;

    private String piano;
    private boolean online;
    private boolean guasto;

    public Macchinetta(String id_macchinetta, int id_istituto, String piano, boolean online, boolean guasto) {
        this.id_macchinetta = id_macchinetta;
        this.id_istituto = id_istituto;
        this.piano = piano;
        this.online = online;
        this.guasto = guasto;
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



    public String getPiano() {
        return piano;
    }

    public void setPiano(String piano) {
        this.piano = piano;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isGuasto() {
        return guasto;
    }

    public void setGuasto(boolean guasto) {
        this.guasto = guasto;
    }

    @Override
    public String toString() {
        return "Macchinetta{" +
                "id_macchinetta='" + id_macchinetta + '\'' +
                ", id_istituto=" + id_istituto +
                ", piano='" + piano + '\'' +
                ", online=" + online +
                ", guasto=" + guasto +
                '}';
    }
}
