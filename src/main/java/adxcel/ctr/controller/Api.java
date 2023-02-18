package adxcel.ctr.controller;

import adxcel.ctr.dto.Aggregate;
import adxcel.ctr.dto.Rate;
import adxcel.ctr.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class Api {
    @Autowired
    private RequestService requestService;

    @GetMapping(value = "ctr/{time}", produces = "application/json")
    public List<Rate> ctr(@PathVariable TimeResolution time) {
        return requestService.getCtr(time);
    }

    @GetMapping(value = "evpm/{event}/{time}", produces = "application/json")
    public List<Rate> evpm(@PathVariable EventType event, @PathVariable TimeResolution time) {
        return requestService.getEvpm(event.name(), event.name(), time);
    }

    @GetMapping(value = "aggr/{groupBy}/{event}", produces = "application/json")
    public List<Aggregate> ctr(@PathVariable AggregateGroup groupBy, @PathVariable EventType event) {
        switch(groupBy) {
            case site:
                return requestService.getAggrBySite(event.name(), event.name());
            case dma:
                return requestService.getAggrByDma(event.name(), event.name());
        }
        return List.of();
    }
}
