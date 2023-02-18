package adxcel.ctr.service;

import adxcel.ctr.controller.EventType;
import adxcel.ctr.controller.TimeResolution;
import adxcel.ctr.dto.Aggregate;
import adxcel.ctr.dto.AggregateByDma;
import adxcel.ctr.dto.AggregateBySite;
import adxcel.ctr.dto.Rate;
import adxcel.ctr.repository.crud.FactRepository;
import adxcel.ctr.repository.projection.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestService {
    @Autowired
    private FactRepository factRepository;

    public List<Rate> getCtr(TimeResolution timeResolution) {
        switch (timeResolution) {
            case day:
                return getRate(factRepository.getCtrGroupByDay());
            case hour:
                return getRate(factRepository.getCtrGroupByHour());
            case minute30:
                return getRate(factRepository.getCtrGroupByMin30());
        }
        return List.of();
    }

    public List<Rate> getEvpm(String eventC, String eventV, TimeResolution timeResolution) {
        switch (timeResolution) {
            case day:
                return getRate(factRepository.getEvpmByEventsGroupByDay(eventC, eventV));
            case hour:
                return getRate(factRepository.getEvpmByEventsGroupByHour(eventC, eventV));
            case minute30:
                return getRate(factRepository.getEvpmByEventsGroupByMin30(eventC, eventV));
        }
        return List.of();
    }

    private List<Rate> getRate(List<RateType> dbRate) {
        return dbRate.stream().map((dbItem) -> new Rate(getTime(dbItem), dbItem.getRate(), dbItem.getImprq())).collect(Collectors.toList());
    }

    private LocalDateTime getTime(DateTimeContainer dbTime) {
        Integer year = dbTime.getImpry();
        if (year == null) {
            throw new IllegalArgumentException("Incorrect time container from DB");
        }
        int month = dbTime.getImprm() == null ? 1 : dbTime.getImprm();
        int day = dbTime.getImprd() == null ? 1 : dbTime.getImprd();
        int hour = dbTime.getImprh() == null ? 0 : dbTime.getImprh();
        int minute30 = dbTime.getImprmin30() == null || dbTime.getImprmin30() == 1 ? 0 : 30;
        return LocalDateTime.of(year, month, day, hour, minute30);
    }

    public List<Aggregate> getAggrByDma(String eventC, String eventV) {
        List<ImprGroupByDmaType> impr = factRepository.getImprQtyGroupByDma();
        List<EventCGroupByDmaType> fclick = factRepository.getEvencQtyGroupByDma(EventType.fclick.name());
        List<EventCGroupByDmaType> eventCList = factRepository.getEvencQtyGroupByDma(eventC);
        List<EventVGroupByDmaType> eventVList = factRepository.getEventVQtyGroupByDma(eventV);
        return getAggregateDto(AggregateByDma::new, impr, fclick, eventCList, eventVList);
    }

    public List<Aggregate> getAggrBySite(String eventC, String eventV) {
        List<ImprGroupBySiteType> impr = factRepository.getImprQtyGroupBySite();
        List<EventCGroupBySiteType> fclick = factRepository.getEvencQtyGroupBySite(EventType.fclick.name());
        List<EventCGroupBySiteType> eventCList = factRepository.getEvencQtyGroupBySite(eventC);
        List<EventVGroupBySiteType> eventVList = factRepository.getEventVQtyGroupBySite(eventV);
        return getAggregateDto(AggregateBySite::new, impr, fclick, eventCList, eventVList);
    }

    private List<Aggregate> getAggregateDto(Supplier<Aggregate> collectorSupplier, List<? extends AggregateType> impr, List<? extends AggregateType> fclick, List<? extends AggregateType> eventC, List<? extends AggregateType> eventV) {
        ArrayList<Aggregate> result = new ArrayList<>();
        Iterator<AggregateType> imprIt = (Iterator<AggregateType>) impr.iterator();
        Iterator<AggregateType> fclickIt = (Iterator<AggregateType>) fclick.iterator();
        Iterator<AggregateType> eventCIt = (Iterator<AggregateType>) eventC.iterator();
        Iterator<AggregateType> eventVIt = (Iterator<AggregateType>) eventV.iterator();
        AggregateType imprCurrent;
        AggregateType fclickCurrent = fclickIt.next();
        AggregateType eventCCurrent = eventCIt.next();
        AggregateType eventVCurrent = eventVIt.next();
        boolean doSearchFclick = true;
        boolean doSearchEventC = true;
        boolean doSearchEventV = true;
        while (imprIt.hasNext()) {
            imprCurrent = imprIt.next();
            long imprQty = imprCurrent.getQty();
            long fclickQty = 0;
            long eventCQty = 0;
            long eventVQty = 0;

            if (doSearchFclick && imprCurrent.getField().equals(fclickCurrent.getField())) {
                fclickQty = fclickCurrent.getQty();
                if (!fclickIt.hasNext()) {
                    doSearchFclick = false;
                } else {
                    fclickCurrent = fclickIt.next();
                }
            }
            if (doSearchEventC && imprCurrent.getField().equals(eventCCurrent.getField())) {
                eventCQty = eventCCurrent.getQty();
                if (!eventCIt.hasNext()) {
                    doSearchEventC = false;
                } else {
                    eventCCurrent = eventCIt.next();
                }
            }
            if (doSearchEventV && imprCurrent.getField().equals(eventVCurrent.getField())) {
                eventVQty = eventVCurrent.getQty();
                if (!eventVIt.hasNext()) {
                    doSearchEventV = false;
                } else {
                    eventVCurrent = eventVIt.next();
                }
            }
            Aggregate collector = collectorSupplier.get();
            collector.setGroup(imprCurrent.getField());
            collector.setImpr(imprQty);
            collector.setCtr((float) fclickQty * 100 / imprQty);
            collector.setEvpm((float) (eventCQty + eventVQty) * 1000 / imprQty);
            result.add(collector);
        }
        if (imprIt.hasNext() || fclickIt.hasNext() || eventCIt.hasNext() || eventVIt.hasNext()) {
            log.warn("Inconsistent data detected at getAggregateDto()");
        }
        return result;
    }
}
