package adxcel.ctr.repository.crud;

import adxcel.ctr.entity.ImpressionFact;
import adxcel.ctr.repository.projection.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactRepository extends CrudRepository<ImpressionFact, Long> {
    ImpressionFact save(ImpressionFact entity);

    List<ImpressionFact> findByUidOrderByTime(String uid);

    /**** CTR ***/
    @Query(value = "select impr.y impry, impr.m imprm, impr.d imprd, coalesce(impr.qty, 0) imprq, cast (coalesce(fclick.qty, 0) as float) * 100 / coalesce(impr.qty, 0) rate\n" +
            "from \n" +
            "(select t.year y, t.month m, t.day d, count(f.id) qty\n" +
            "from impression_fact f, time t\n" +
            "where f.time_id = t.id\n" +
            "group by t.year, t.month, t.day\n" +
            "order by t.year, t.month, t.day) as impr\n" +
            "left join\n" +
            "(select t.year y, t.month m, t.day d, sum(hc.qty) qty\n" +
            "from impression_fact f, eventc ec, happenc hc, time t\n" +
            "where f.time_id = t.id and hc.fact_id = f.id and hc.eventc_id = ec.id and ec.event = 'fclick'\n" +
            "group by t.year, t.month, t.day\n" +
            "order by t.year, t.month, t.day\n" +
            ") as fclick\n" +
            "on\n" +
            "impr.y = fclick.y and impr.m = fclick.m and impr.d = fclick.d\n",
            nativeQuery = true)
    List<RateType> getCtrGroupByDay();

    @Query(value = "select impr.y impry, impr.m imprm, impr.d imprd, impr.h imprh, coalesce(impr.qty, 0) imprq, cast (coalesce(fclick.qty, 0) as float) * 100 / coalesce(impr.qty, 0) rate\n" +
            "from \n" +
            "(select t.year y, t.month m, t.day d, t.hour h, count(f.id) qty\n" +
            "from impression_fact f, time t\n" +
            "where f.time_id = t.id\n" +
            "group by t.year, t.month, t.day, t.hour\n" +
            "order by t.year, t.month, t.day, t.hour) as impr\n" +
            "left join\n" +
            "(select t.year y, t.month m, t.day d, t.hour h, sum(hc.qty) qty\n" +
            "from impression_fact f, eventc ec, happenc hc, time t\n" +
            "where f.time_id = t.id and hc.fact_id = f.id and hc.eventc_id = ec.id and ec.event = 'fclick'\n" +
            "group by t.year, t.month, t.day, t.hour\n" +
            "order by t.year, t.month, t.day, t.hour\n" +
            ") as fclick\n" +
            "on\n" +
            "impr.y = fclick.y and impr.m = fclick.m and impr.d = fclick.d and impr.h = fclick.h\n",
            nativeQuery = true)
    List<RateType> getCtrGroupByHour();

    @Query(value = "select impr.y impry, impr.m imprm, impr.d imprd, impr.h imprh, impr.minute30 imprmin30, coalesce(impr.qty, 0) imprq, cast (coalesce(fclick.qty, 0) as float) * 100 / coalesce(impr.qty, 0) rate\n" +
            "from \n" +
            "(select t.year y, t.month m, t.day d, t.hour h, t.minute30 minute30, count(f.id) qty\n" +
            "from impression_fact f, time t\n" +
            "where f.time_id = t.id\n" +
            "group by t.year, t.month, t.day, t.hour, t.minute30\n" +
            "order by t.year, t.month, t.day, t.hour, t.minute30) as impr\n" +
            "left join\n" +
            "(select t.year y, t.month m, t.day d, t.hour h, t.minute30 minute30, sum(hc.qty) qty\n" +
            "from impression_fact f, eventc ec, happenc hc, time t\n" +
            "where f.time_id = t.id and hc.fact_id = f.id and hc.eventc_id = ec.id and ec.event = 'fclick'\n" +
            "group by t.year, t.month, t.day, t.hour, t.minute30\n" +
            "order by t.year, t.month, t.day, t.hour, t.minute30\n" +
            ") as fclick\n" +
            "on\n" +
            "impr.y = fclick.y and impr.m = fclick.m and impr.d = fclick.d and impr.h = fclick.h and impr.minute30 = fclick.minute30\n",
            nativeQuery = true)
    List<RateType> getCtrGroupByMin30();

    /**** EvPM ***/
    @Query(value = "select impry, imprm, imprd, coalesce(total, 0) imprq, cast ((coalesce(qtyc, 0) + coalesce(qtyv, 0)) as float) * 1000 / coalesce(total, 0) as rate\n" +
            "from \n" +
            "\t(select t.year impry, t.month imprm, t.day imprd, count(f.id) total\n" +
            "\tfrom impression_fact f, time t\n" +
            "\twhere f.time_id = t.id\n" +
            "\tgroup by t.year, t.month, t.day\n" +
            "\torder by t.year, t.month, t.day) as impressions\n" +
            "left join\n" +
            "\t\t\t(select t.year evy, t.month evm, t.day evd, sum(h.hcq) qtyc, sum(h.hvq) qtyv\n" +
            "\t\t\tfrom impression_fact f, time t,\n" +
            "\t\t\t(select ec.id ecid, ec.event ec, ev.id evid, ev.event ev\n" +
            "\t\t\tfrom eventc ec\n" +
            "\t\t\tfull outer join eventv ev on false\n" +
            "\t\t\twhere ec.event = ?1 or ev.event = ?2) as e,\n" +
            "\t\t\t(select hc.id hcid, hc.qty hcq, hc.eventc_id hcecid, hc.fact_id hcfid, hv.id hvid, hv.qty hvq, hv.eventv_id hvevid, hv.fact_id hvfid \n" +
            "\t\t\tfrom happenc hc\n" +
            "\t\t\tfull outer join happenv hv on false) as h\n" +
            "\t\t\twhere (f.time_id = t.id and h.hcfid = f.id and h.hcecid = e.ecid)\n" +
            "\t\t\tor (f.time_id = t.id and h.hvfid = f.id and h.hvevid = e.evid)\n" +
            "\t\t\tgroup by t.year, t.month, t.day\n" +
            "\t\t\torder by t.year, t.month, t.day\n" +
            "\t\t\t) as events\n" +
            "on impry = evy and imprm = evm and imprd = evd\n",
            nativeQuery = true)
    List<RateType> getEvpmByEventsGroupByDay(String eventC, String eventV);

    @Query(value = "select impry, imprm, imprd, imprh, coalesce(total, 0) imprq, cast ((coalesce(qtyc, 0) + coalesce(qtyv, 0)) as float) * 1000 / coalesce(total, 0) as rate\n" +
            "from \n" +
            "\t(select t.year impry, t.month imprm, t.day imprd, t.hour imprh, count(f.id) total\n" +
            "\tfrom impression_fact f, time t\n" +
            "\twhere f.time_id = t.id\n" +
            "\tgroup by t.year, t.month, t.day, t.hour\n" +
            "\torder by t.year, t.month, t.day, t.hour) as impressions\n" +
            "left join\n" +
            "\t\t\t(select t.year evy, t.month evm, t.day evd, t.hour evh, sum(h.hcq) qtyc, sum(h.hvq) qtyv\n" +
            "\t\t\tfrom impression_fact f, time t,\n" +
            "\t\t\t(select ec.id ecid, ec.event ec, ev.id evid, ev.event ev\n" +
            "\t\t\tfrom eventc ec\n" +
            "\t\t\tfull outer join eventv ev on false\n" +
            "\t\t\twhere ec.event = ?1 or ev.event = ?2) as e,\n" +
            "\t\t\t(select hc.id hcid, hc.qty hcq, hc.eventc_id hcecid, hc.fact_id hcfid, hv.id hvid, hv.qty hvq, hv.eventv_id hvevid, hv.fact_id hvfid \n" +
            "\t\t\tfrom happenc hc\n" +
            "\t\t\tfull outer join happenv hv on false) as h\n" +
            "\t\t\twhere (f.time_id = t.id and h.hcfid = f.id and h.hcecid = e.ecid)\n" +
            "\t\t\tor (f.time_id = t.id and h.hvfid = f.id and h.hvevid = e.evid)\n" +
            "\t\t\tgroup by t.year, t.month, t.day, t.hour\n" +
            "\t\t\torder by t.year, t.month, t.day, t.hour\n" +
            "\t\t\t) as events\n" +
            "on impry = evy and imprm = evm and imprd = evd and imprh = evh\n",
            nativeQuery = true)
    List<RateType> getEvpmByEventsGroupByHour(String eventC, String eventV);

    @Query(value = "select impry, imprm, imprd, imprh, imprmin30, coalesce(total, 0) imprq, cast ((coalesce(qtyc, 0) + coalesce(qtyv, 0)) as float) * 1000 / coalesce(total, 0) as rate\n" +
            "from \n" +
            "\t(select t.year impry, t.month imprm, t.day imprd, t.hour imprh, t.minute30 imprmin30, count(f.id) total\n" +
            "\tfrom impression_fact f, time t\n" +
            "\twhere f.time_id = t.id\n" +
            "\tgroup by t.year, t.month, t.day, t.hour, t.minute30\n" +
            "\torder by t.year, t.month, t.day, t.hour, t.minute30) as impressions\n" +
            "left join\n" +
            "\t\t\t(select t.year evy, t.month evm, t.day evd, t.hour evh, t.minute30 ev30, sum(h.hcq) qtyc, sum(h.hvq) qtyv\n" +
            "\t\t\tfrom impression_fact f, time t,\n" +
            "\t\t\t(select ec.id ecid, ec.event ec, ev.id evid, ev.event ev\n" +
            "\t\t\tfrom eventc ec\n" +
            "\t\t\tfull outer join eventv ev on false\n" +
            "\t\t\twhere ec.event = ?1 or ev.event = ?2) as e,\n" +
            "\t\t\t(select hc.id hcid, hc.qty hcq, hc.eventc_id hcecid, hc.fact_id hcfid, hv.id hvid, hv.qty hvq, hv.eventv_id hvevid, hv.fact_id hvfid \n" +
            "\t\t\tfrom happenc hc\n" +
            "\t\t\tfull outer join happenv hv on false) as h\n" +
            "\t\t\twhere (f.time_id = t.id and h.hcfid = f.id and h.hcecid = e.ecid)\n" +
            "\t\t\tor (f.time_id = t.id and h.hvfid = f.id and h.hvevid = e.evid)\n" +
            "\t\t\tgroup by t.year, t.month, t.day, t.hour, t.minute30\n" +
            "\t\t\torder by t.year, t.month, t.day, t.hour, t.minute30\n" +
            "\t\t\t) as events\n" +
            "on impry = evy and imprm = evm and imprd = evd and imprh = evh and imprmin30 = ev30\n",
            nativeQuery = true)
    List<RateType> getEvpmByEventsGroupByMin30(String eventC, String eventV);


    // Aggregates by site
    @Query(value = "select s.site field, sum(hc.qty) qty from impression_fact f, happenc hc, eventc ec, site s\n" +
            "where s.id = f.site_id and f.id = hc.fact_id and hc.eventc_id = ec.id and ec.event = ?1\n" +
            "group by s.id\n" +
            "order by s.site",
            nativeQuery = true)
    List<EventCGroupBySiteType> getEvencQtyGroupBySite(String eventName);

    @Query(value = "select s.site field, sum(hv.qty) qty from impression_fact f, happenv hv, eventv ev, site s\n" +
            "where s.id = f.site_id and f.id = hv.fact_id and hv.eventv_id = ev.id and ev.event = ?1\n" +
            "group by s.id\n" +
            "order by s.site",
            nativeQuery = true)
    List<EventVGroupBySiteType> getEventVQtyGroupBySite(String eventName);

    @Query(value = "select s.site field, count(f.id) qty from impression_fact f, site s\n" +
            "where s.id = f.site_id\n" +
            "group by s.id\n" +
            "order by s.site",
            nativeQuery = true)
    List<ImprGroupBySiteType> getImprQtyGroupBySite();


    // Aggregates by dma
    @Query(value = "select d.dma field, sum(hc.qty) qty from impression_fact f, happenc hc, eventc ec, dma d\n" +
            "where d.id = f.dma_id and f.id = hc.fact_id and hc.eventc_id = ec.id and ec.event = ?1\n" +
            "group by d.id\n" +
            "order by d.dma",
            nativeQuery = true)
    List<EventCGroupByDmaType> getEvencQtyGroupByDma(String eventName);

    @Query(value = "select d.dma field, sum(hv.qty) qty from impression_fact f, happenv hv, eventv ev, dma d\n" +
            "where d.id = f.dma_id and f.id = hv.fact_id and hv.eventv_id = ev.id and ev.event = ?1\n" +
            "group by d.id\n" +
            "order by d.dma",
            nativeQuery = true)
    List<EventVGroupByDmaType> getEventVQtyGroupByDma(String eventName);

    @Query(value = "select d.dma field, count(f.id) qty from impression_fact f, dma d\n" +
            "where d.id = f.dma_id\n" +
            "group by d.id\n" +
            "order by d.dma",
            nativeQuery = true)
    List<ImprGroupByDmaType> getImprQtyGroupByDma();
}
