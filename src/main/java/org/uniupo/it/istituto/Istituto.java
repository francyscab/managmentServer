package org.uniupo.it.istituto;

import java.sql.Timestamp;

public class Istituto {
    private int id_istituto;
    private String nome;
    private String indirizzo;
    private String citta;
    private Timestamp data_creazione;

    public Istituto(int id_istituto, String nome, String indirizzo, String citta, Timestamp data_creazione) {
        this.id_istituto = id_istituto;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.data_creazione = data_creazione;
    }

    public Istituto(String nome, String indirizzo, String citta) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = citta;
    }


    public Istituto() {
    }

    public int getId_istituto() {
        return id_istituto;
    }

    public void setId_istituto(int id_istituto) {
        this.id_istituto = id_istituto;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public Timestamp getData_creazione() {
        return data_creazione;
    }

    public void setData_creazione(Timestamp data_creazione) {
        this.data_creazione = data_creazione;
    }

    @Override
    public String toString() {
        return "Istituto{" + "id_istituto=" + id_istituto + ", nome='" + nome + '\'' + ", indirizzo='" + indirizzo + '\'' + ", citta='" + citta + '\'' + ", data_creazione=" + data_creazione + '}';
    }
}
