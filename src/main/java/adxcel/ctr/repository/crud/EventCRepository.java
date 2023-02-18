package adxcel.ctr.repository.crud;

import adxcel.ctr.entity.EventC;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCRepository extends CrudRepository<EventC, Long> {
    EventC save(EventC entity);
}
