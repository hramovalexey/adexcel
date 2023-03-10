package adxcel.ctr.service.dataimport;

import adxcel.ctr.dto.ImportReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

@Slf4j
public class CsvFileImporter {
    private final InputStream inputStream;
    private final BiConsumer<String[], ImportReport> dbImporter;
    private final ImportReport report;

    public CsvFileImporter(String path, BiConsumer<String[], ImportReport> dbImporter, ImportReport report) throws IOException {
        this.inputStream = new ClassPathResource("classpath:" + path).getInputStream();
        this.dbImporter = dbImporter;
        this.report = report;
    }

    public void importCsv() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            log.info("Started import " + report.getTableType());
            reader.readLine(); // skip header
            String line;
            long start = System.currentTimeMillis();
            while ((line = reader.readLine()) != null) {
                String[] tuple = line.split(",");
                dbImporter.accept(tuple, report); // TODO asynchronous task can be implemented here

                if (report.incrementAndGetSourceRows() % 1000 == 0) {

                    long end = System.currentTimeMillis();
                    log.info("Processed rows " + report.getSourceRowsProcessed() + " for " + (end - start) + " ms");
                    start = end;
                }

            }
            log.info("Finished import " + report.getTableType());
        }

    }
}