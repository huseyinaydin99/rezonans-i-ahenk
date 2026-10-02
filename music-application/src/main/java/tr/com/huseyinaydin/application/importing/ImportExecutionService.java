package tr.com.huseyinaydin.application.importing;

import org.springframework.stereotype.Service;

@Service
public class ImportExecutionService {

    public void executeImport() {
        BaseBulkImporter csvImporter = new CsvBulkImporter();
        csvImporter.importData("/uploads/music_catalog.csv");

        BaseBulkImporter jsonImporter = new JsonBulkImporter();
        jsonImporter.importData("/uploads/music_catalog.json");
    }
}
