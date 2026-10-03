package tr.com.huseyinaydin.infrastructure.nosql.service;

import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.infrastructure.nosql.table.ListeningHistoryTable;

@Service
public class CassandraListeningHistoryService {

    /**
     * Saniyede milyonlarca kez çağrılabilecek bu metot, veritabanı kilidi (Lock) 
     * oluşturmadan veriyi Big Data kümesine (Cassandra) ışık hızında yazar.
     */
    public void saveHistory(ListeningHistoryTable historyTable) {
        System.out.println("[CASSANDRA] Olay verisi NoSQL (Wide-Column) kümesine yazıldı. " +
                "Kullanıcı: " + historyTable.toString());
        // Gerçekte CassandraRepository.save() çalışır
    }
}
