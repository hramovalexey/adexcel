package adxcel.ctr;

import adxcel.ctr.dto.ImportReport;
import adxcel.ctr.dto.ImportReportImpl;
import adxcel.ctr.repository.crud.*;
import adxcel.ctr.service.UploadService;
import adxcel.ctr.service.dataimport.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DbInitializer {

    @Autowired
    private DmaRepository dmaRepository;
    @Autowired
    private EventCRepository eventCRepository;
    @Autowired
    private EventVRepository eventVRepository;
    @Autowired
    private FactRepository factRepository;
    @Autowired
    private HappenCRepository happenCRepository;
    @Autowired
    private HappenVRepository happenVRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ImpressionDbImporter impressionDbImporter;
    @Autowired
    private EventDbImporter eventDbImporter;
    @Autowired
    private UploadService uploadService;

    public void intDb() throws IOException {
        try {
            boolean isEmptyDb = isEmpty(dmaRepository) &&
                    isEmpty(eventCRepository) &&
                    isEmpty(eventVRepository) &&
                    isEmpty(factRepository) &&
                    isEmpty(happenCRepository) &&
                    isEmpty(happenVRepository) &&
                    isEmpty(siteRepository) &&
                    isEmpty(timeRepository);
            if (isEmptyDb) {
                log.info("Database 'ctr' is empty. Trying to populate DB...");
                populateDb();
            } else {
                log.info("Database 'ctr' has been populated already. Starting application");
            }
        } catch(Exception e) {
            log.error("Database 'ctr' must be present and be empty before initialization");
            throw e;
        }

    }

    private boolean isEmpty(CrudRepository<?, ?> repository) {
        return repository.count() == 0;
    }

    private void populateDb() throws IOException {
        ImportReport importReportImpr = new ImportReportImpl(TableType.IMPRESSION);
        ImportReport importReportEvents = new ImportReportImpl(TableType.EVENT);
        long start = System.currentTimeMillis();
        uploadService.uploadImpressions(new CsvFileImporter("interview.X.csv", impressionDbImporter, importReportImpr));
        long endImpr = System.currentTimeMillis();
        log.info("Impression uploaded for " + (endImpr - start) + " ms");
        uploadService.uploadEvents(new CsvFileImporter("interview.y.csv", eventDbImporter, importReportEvents));
        long endEvent = System.currentTimeMillis();
        log.info("Event uploaded for " + (endEvent - endImpr) + " ms");
        log.info("Impressions import report: " + importReportImpr.toString());
        log.info("Events import report: " + importReportEvents.toString());
    }

}
