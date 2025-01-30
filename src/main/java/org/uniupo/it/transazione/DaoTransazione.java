package org.uniupo.it.transazione;

import java.util.List;

public interface DaoTransazione {
    void addTransazione(TransactionMessage transazione);

    List<TransactionMessage> getTransazioniByMacchinetta(String idMacchinetta, String idIstituto);

    List<TransactionMessage> getTransazioniByIstituto(String idIstituto);

    TransactionMessage getTransazioneById(int id);

    List<TransactionMessage> getAllTransazioni();
}