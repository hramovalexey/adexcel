package adxcel.ctr.repository.crud;

import adxcel.ctr.entity.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HappenCRepository extends CrudRepository<HappenC, Long> {
    HappenC save(HappenC entity);
    Optional<HappenC> findFirstByFactAndEventC(ImpressionFact fact, EventC eventC);
}
