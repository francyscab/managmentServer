package org.uniupo.it.ricavo;

import java.sql.Timestamp;
import java.math.BigDecimal;

public class Ricavo {
    private int id_ricavo;
    private String id_macchinetta;
    private BigDecimal somma_ricavo;
    private Timestamp data_ricavo;
    private String raccolto_da;

    public Ricavo(int id_ricavo, String id_macchinetta, BigDecimal somma_ricavo, Timestamp data_ricavo, String raccolto_da) {
        this.id_ricavo = id_ricavo;
        this.id_macchinetta = id_macchinetta;
        this.somma_ricavo = somma_ricavo;
        this.data_ricavo = data_ricavo;
        this.raccolto_da = raccolto_da;
    }

    public Ricavo(String id_macchinetta, BigDecimal somma_ricavo, String raccolto_da) {
        this.id_macchinetta = id_macchinetta;
        this.somma_ricavo = somma_ricavo;
        this.raccolto_da = raccolto_da;
    }

    public int getId_ricavo() {
        return id_ricavo;
    }

    public void setId_ricavo(int id_ricavo) {
        this.id_ricavo = id_ricavo;
    }

    public String getId_macchinetta() {
        return id_macchinetta;
    }

    public void setId_macchinetta(String id_macchinetta) {
        this.id_macchinetta = id_macchinetta;
    }

    public BigDecimal getSomma_ricavo() {
        return somma_ricavo;
    }

    public void setSomma_ricavo(BigDecimal somma_ricavo) {
        this.somma_ricavo = somma_ricavo;
    }

    public Timestamp getData_ricavo() {
        return data_ricavo;
    }

    public void setData_ricavo(Timestamp data_ricavo) {
        this.data_ricavo = data_ricavo;
    }

    public String getRaccolto_da() {
        return raccolto_da;
    }

    public void setRaccolto_da(String raccolto_da) {
        this.raccolto_da = raccolto_da;
    }

    @Override
    public String toString() {
        return "Ricavo{" +
                "id_ricavo=" + id_ricavo +
                ", id_macchinetta='" + id_macchinetta + '\'' +
                ", somma_ricavo=" + somma_ricavo +
                ", data_ricavo=" + data_ricavo +
                ", raccolto_da='" + raccolto_da + '\'' +
                '}';
    }
}