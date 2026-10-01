package tr.com.huseyinaydin.web.jsf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.importdata.commands.ImportSongsCommand;

@Named
@RequestScoped
public class ImportBean {

    @Autowired
    private Mediator mediator;

    private String csvData;
    private String importMessage;

    public void processImport() {
        if (csvData != null && !csvData.trim().isEmpty()) {
            Integer count = mediator.send(new ImportSongsCommand(csvData));
            this.importMessage = count + " adet şarkı başarıyla sisteme aktarıldı!";
            this.csvData = ""; // Clear after import
        } else {
            this.importMessage = "Lütfen aktarılacak CSV verisini giriniz.";
        }
    }

    public String getCsvData() { return csvData; }
    public void setCsvData(String csvData) { this.csvData = csvData; }
    public String getImportMessage() { return importMessage; }
}
