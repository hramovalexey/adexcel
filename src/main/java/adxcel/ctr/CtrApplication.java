package adxcel.ctr;

import adxcel.ctr.service.RequestService;
import adxcel.ctr.service.UploadService;
import adxcel.ctr.service.dataimport.*;
import adxcel.ctr.repository.crud.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication
public class CtrApplication {
    @Autowired
    private UploadService uploadService;
    @Autowired
    private ImpressionDbImporter impressionDbImporter;
    @Autowired
    private EventDbImporter eventDbImporter;
    @Autowired
    private FactRepository factRepository;
    @Autowired
    private RequestService requestService;
    @Autowired
    private DbInitializer dbInitializer;

    @PostConstruct
    private void appInit() throws IOException {
        dbInitializer.intDb();
    }

    public static void main(String[] args) {
        SpringApplication.run(CtrApplication.class, args);
    }

}
