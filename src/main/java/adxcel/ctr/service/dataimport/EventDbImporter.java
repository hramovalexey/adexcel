package adxcel.ctr.service.dataimport;

import adxcel.ctr.dto.ImportReport;
import adxcel.ctr.entity.*;
import adxcel.ctr.repository.crud.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@Component
public class EventDbImporter implements BiConsumer<String[], ImportReport> {
    @Autowired
    private EventCRepository eventCRepository;
    @Autowired
    private EventVRepository eventVRepository;
    @Autowired
    private FactRepository factRepository;
    @Autowired
    private HappenVRepository happenVRepository;
    @Autowired
    private HappenCRepository happenCRepository;

    private final HashMap<String, EventC> eventCMap;
    private final HashMap<String, EventV> eventVMap;
    private HashMap<String, HashMap<String, HappenC>> happenCMap; // "uid" -> ("event" -> happenC)
    private HashMap<String, HashMap<String, HappenV>> happenVMap; // "uid" -> ("event" -> happenV)

    public EventDbImporter() {

        // TODO actually maps must be initialized by existing values from DB
        // I ignore this because we are dealing with test dataset which is uploaded only once
        this.eventCMap = new HashMap<>();
        this.eventVMap = new HashMap<>();
    }

    @Transactional
    @Override
    public void accept(String[] tuple, ImportReport report) {
        if (tuple.length != 2) {
            throw new IllegalArgumentException("Wrong arg length at " + Arrays.toString(tuple));
        }
        String tag = tuple[1];
        String uid = tuple[0];
        if (tag.startsWith("v")) {
            EventV eventV = eventVMap.computeIfAbsent(tag.substring(1), (t) -> eventVRepository.save(new EventV(t)));
            Optional.ofNullable(findFactByUid(uid, report)).ifPresent((fact) -> {
                HappenV happenV = happenVRepository.findFirstByFactAndEventV(fact, eventV)
                        .orElse(new HappenV(fact, eventV, 0));
                happenV.incrementQtyAndGet();
                happenVRepository.save(happenV);
                report.incrementAndGetImportedRows();
            });
        } else {
            EventC eventC = eventCMap.computeIfAbsent(tag, (t) -> eventCRepository.save(new EventC(t)));
            Optional.ofNullable(findFactByUid(uid, report)).ifPresent((fact) -> {
                HappenC happenC = happenCRepository.findFirstByFactAndEventC(fact, eventC)
                        .orElse(new HappenC(fact, eventC, 0));
                happenC.incrementQtyAndGet();
                happenCRepository.save(happenC);
                report.incrementAndGetImportedRows();
            });
        }
    }

    private ImpressionFact findFactByUid(String uid, ImportReport report) {
        List<ImpressionFact> uids = factRepository.findByUidOrderByTime(uid);
        if (uids.size() > 1) {
            report.getUidCollisions().add(uid);
            ImpressionFact resolved = resolveUidCollision(uids);
            if (resolved == null) {
                report.getUnresolvedUidCollisions().add(uid);
            }
            return resolved;
        }
        if (uids.size() == 0) {
            report.getUnmappedUids().add(uid);
            return null;
        }
        return uids.get(0);
    }

    private ImpressionFact resolveUidCollision(List<ImpressionFact> uids) {
        if (uids.size() < 2) {
            throw new IllegalArgumentException("Unable to resolve uids collision having only one impression fact");
        }
        int diff = 0;
        for (int i = 1; i < uids.size(); i++) {
            diff += Duration.between(uids.get(i - 1).getTime().getTimeStamp(), uids.get(i).getTime().getTimeStamp()).getSeconds();
        }
        if (diff < 100) { // less than 100 seconds
            return uids.get(uids.size() - 1);
        } else {
            return null;
        }
    }


}
