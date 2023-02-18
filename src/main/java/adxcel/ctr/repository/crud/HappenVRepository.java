package adxcel.ctr.repository.crud;

import adxcel.ctr.entity.Dma;
import adxcel.ctr.entity.EventV;
import adxcel.ctr.entity.HappenV;
import adxcel.ctr.entity.ImpressionFact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HappenVRepository extends CrudRepository<HappenV, Long> {
    HappenV save(HappenV entity);
    Optional<HappenV> findFirstByFactAndEventV(ImpressionFact fact, EventV eventV);
}
