package adxcel.ctr.service.dataimport;

import adxcel.ctr.dto.ImportReport;
import adxcel.ctr.entity.*;
import adxcel.ctr.repository.crud.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

@Component
public class ImpressionDbImporter implements BiConsumer<String[], ImportReport> {
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private DmaRepository dmaRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private FactRepository factRepository;
    @Autowired
    private EventCRepository eventCRepository;
    @Autowired
    private EventCRepository eventVRepository;


    private final DateTimeFormatter oneHourDigitFormatter;
    private final DateTimeFormatter twoHourDigitFormatter;
    private final Pattern oneHourPattern;
    private final Pattern twoHourPattern;
    private final HashMap<String, Time> timeMap;
    private final HashMap<String, Site> siteMap;
    private final HashMap<String, Dma> dmaMap;

    public ImpressionDbImporter() {
        this.oneHourDigitFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");
        this.twoHourDigitFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.oneHourPattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{1}:\\d{2}:\\d{2}$");
        this.twoHourPattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");

        // TODO actually maps must be initialized by existing values from DB
        // I ignore this because we are dealing with test dataset which is uploaded only once
        this.timeMap = new HashMap<>();
        this.siteMap = new HashMap<>();
        this.dmaMap = new HashMap<>();
    }

    @Override
    public void accept(String[] tuple, ImportReport report) {
        if (tuple.length != 10) {
            throw new IllegalArgumentException("Wrong arg length at " + Arrays.toString(tuple));
        }
        Time time = timeMap.computeIfAbsent(tuple[0], this::importTime);
        Dma dma = dmaMap.computeIfAbsent(tuple[5], this::importDma);
        Site site = siteMap.computeIfAbsent(tuple[9], this::importSite);
        importFact(time, tuple[1], dma, site);
        report.incrementAndGetImportedRows();
    }

    private Time importTime(String str) {
        LocalDateTime dateTime = parseTime(str);
        int minute = dateTime.getMinute();
        int minute30 = minute < 30 ? 1 : 2;
        int hour = dateTime.getHour();
        int day = dateTime.getDayOfMonth();
        int dayOfWeek = dateTime.getDayOfWeek().ordinal();
        int month = dateTime.getMonthValue();
        int year = dateTime.getYear();

        Time time = new Time(dateTime, minute, minute30, hour, day, dayOfWeek, month, year);
        return timeRepository.save(time);
    }

    private LocalDateTime parseTime(String str) {
        LocalDateTime dateTime;
        if (twoHourPattern.matcher(str).matches()) {
            dateTime = LocalDateTime.parse(str, twoHourDigitFormatter);
        } else if (oneHourPattern.matcher(str).matches()) {
            dateTime = LocalDateTime.parse(str, oneHourDigitFormatter);
        } else {
            throw new IllegalArgumentException("Illegal data format: " + str);
        }
        return dateTime;
    }

    private Dma importDma(String str) {
        Dma dma = new Dma(Integer.parseInt(str));
        return dmaRepository.save(dma);
    }

    private Site importSite(String str) {
        return siteRepository.save(new Site(str));
    }

    private ImpressionFact importFact(Time time, String uid, Dma dma, Site site) {
        return factRepository.save(new ImpressionFact(uid, time, site, dma));
    }

}
