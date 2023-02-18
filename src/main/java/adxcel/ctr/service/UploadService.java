package adxcel.ctr.service;

import adxcel.ctr.service.dataimport.CsvFileImporter;
import adxcel.ctr.service.dataimport.EventDbImporter;
import adxcel.ctr.service.dataimport.ImpressionDbImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class UploadService {

    @Transactional
    public void uploadImpressions(CsvFileImporter importer) throws IOException {
        importer.importCsv();
    }

    public void uploadEvents(CsvFileImporter importer) throws IOException {
        importer.importCsv();
    }
}
